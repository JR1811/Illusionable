package net.shirojr.illusionable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.illusionable.cca.implementation.PlayerViewComponent;
import net.shirojr.illusionable.command.argument.ViewArgumentType;
import net.shirojr.illusionable.command.util.View;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlayerViewCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("view").requires(source -> source.hasPermissionLevel(2))
                .then(literal("set")
                        .then(literal("view")
                                .then(argument("view", ViewArgumentType.view())
                                        .then(argument("lock", BoolArgumentType.bool())
                                                .then(argument("targets", EntityArgumentType.players())
                                                        .executes(PlayerViewCommand::setView)
                                                )
                                        )
                                )
                        )
                        .then(literal("lock")
                                .then(argument("lock", BoolArgumentType.bool())
                                        .then(argument("targets", EntityArgumentType.players())
                                                .executes(PlayerViewCommand::setLock)
                                        )
                                )
                        )
                )
        );
    }

    private static int setLock(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        boolean lock = BoolArgumentType.getBool(context, "lock");
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
        targets.forEach(target -> PlayerViewComponent.get(target).setLocked(lock));
        context.getSource().sendFeedback(() -> Text.literal("Set Player View lock for targets to: " + lock), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setView(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        View view = ViewArgumentType.getView(context, "view");
        boolean lock = BoolArgumentType.getBool(context, "lock");
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
        targets.forEach(target -> PlayerViewComponent.get(target).setView(view, true, lock));
        context.getSource().sendFeedback(() -> Text.literal("Applied %s view to targets".formatted(view.asString())), true);
        return Command.SINGLE_SUCCESS;
    }
}
