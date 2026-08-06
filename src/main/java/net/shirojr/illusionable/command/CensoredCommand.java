package net.shirojr.illusionable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.shirojr.illusionable.cca.implementation.CensoredComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CensoredCommand implements CommandRegistrationCallback {
    private static final SimpleCommandExceptionType NO_SOURCES =
            new SimpleCommandExceptionType(Text.literal("No censor sources found"));
    private static final SimpleCommandExceptionType NO_TARGETS =
            new SimpleCommandExceptionType(Text.literal("No censor targets found"));

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("censor")
                .then(literal("set")
                        .then(argument("sources", EntityArgumentType.players())
                                .then(argument("targets", EntityArgumentType.entities())
                                        .then(argument("scale", FloatArgumentType.floatArg(0))
                                                .executes(CensoredCommand::setCensor)
                                        )
                                )
                        )
                )
                .then(literal("remove")
                        .then(argument("sources", EntityArgumentType.players())
                                .executes(context -> CensoredCommand.removeCensor(context, null))
                                .then(argument("targets", EntityArgumentType.entities())
                                        .executes(context -> CensoredCommand.removeCensor(context, EntityArgumentType.getEntities(context, "targets")))
                                )
                        )
                )
        );
    }

    private static int removeCensor(CommandContext<ServerCommandSource> context, @Nullable Collection<? extends Entity> targets) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> sources = EntityArgumentType.getPlayers(context, "sources");
        if (sources.isEmpty()) throw NO_SOURCES.create();
        for (ServerPlayerEntity source : sources) {
            CensoredComponent component = CensoredComponent.get(source);
            if (targets == null) {
                component.clear();
            } else {
                component.remove(targets);
            }
            context.getSource().sendFeedback(() ->
                    Text.literal("Removed all entries for %s".formatted(source.getName().getString())), true
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int setCensor(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> sources = EntityArgumentType.getPlayers(context, "sources");
        if (sources.isEmpty()) throw NO_SOURCES.create();
        Collection<? extends Entity> targets = EntityArgumentType.getEntities(context, "targets");
        if (targets.isEmpty()) throw NO_TARGETS.create();
        float scale = FloatArgumentType.getFloat(context, "scale");
        for (ServerPlayerEntity source : sources) {
            CensoredComponent component = CensoredComponent.get(source);
            component.add(targets, scale);
            context.getSource().sendFeedback(() ->
                    Text.literal("%s changed censor targets with a scale of %s".formatted(source.getName().getString(), scale)), true
            );
        }
        return Command.SINGLE_SUCCESS;
    }
}
