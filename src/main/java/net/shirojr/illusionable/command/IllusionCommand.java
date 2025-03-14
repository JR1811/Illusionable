package net.shirojr.illusionable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IllusionCommand {
    public static final String ILLUSION_KEY = "illusion", ILLUSION_STATE_KEY = "illusionState", VICTIMS_KEY = "victims";

    private static final SimpleCommandExceptionType NOT_ILLUSIONABLE =
            new SimpleCommandExceptionType(Text.literal("Entity can't be an illusion"));
    private static final SimpleCommandExceptionType NO_VICTIMS_AVAILABLE =
            new SimpleCommandExceptionType(Text.literal("No entries in victims list were applicable"));


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess dedicated, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("illusion").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument(ILLUSION_KEY, EntityArgumentType.entities())
                                .then(argument(ILLUSION_STATE_KEY, BoolArgumentType.bool())
                                        .executes(IllusionCommand::setIllusionSate))))
                .then(literal("add")
                        .then(argument(ILLUSION_KEY, EntityArgumentType.entities())
                                .then(argument(VICTIMS_KEY, EntityArgumentType.entities())
                                        .executes(IllusionCommand::addIllusionTargets))))
                .then(literal("remove")
                        .then(argument(ILLUSION_KEY, EntityArgumentType.entities())
                                .executes(IllusionCommand::clearAllIllusionTargets)
                                .then(argument(VICTIMS_KEY, EntityArgumentType.entities())
                                        .executes(IllusionCommand::clearIllusionTargets)))));
    }


    private static int setIllusionSate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<Entity> illusions = new ArrayList<>(EntityArgumentType.getEntities(context, ILLUSION_KEY));
        boolean isIllusion = BoolArgumentType.getBool(context, ILLUSION_STATE_KEY);
        for (Entity illusion : illusions) {
            if (!(illusion instanceof IllusionHandler illusionRendering)) throw NOT_ILLUSIONABLE.create();
            illusionRendering.illusionable$setIllusion(isIllusion);
            if (!isIllusion) illusionRendering.illusionable$clearIllusionTargets();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addIllusionTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<Entity> illusions = new ArrayList<>(EntityArgumentType.getEntities(context, ILLUSION_KEY));
        List<Entity> victims = new ArrayList<>(EntityArgumentType.getEntities(context, VICTIMS_KEY));
        if (victims.isEmpty()) throw NO_VICTIMS_AVAILABLE.create();

        for (Entity illusion : illusions) {
            if (!(illusion instanceof IllusionHandler illusionRendering)) throw NOT_ILLUSIONABLE.create();
            illusionRendering.illusionable$modifyIllusionTargets(uuids -> uuids.addAll(victims.stream().map(Entity::getUuid).toList()));

            StringBuilder sb = new StringBuilder("Added");
            victims.forEach(entity -> sb.append(" ").append(entity.getName().getString()));
            context.getSource().sendFeedback(() -> Text.literal(sb.toString()), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int clearAllIllusionTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> illusions = EntityArgumentType.getEntities(context, ILLUSION_KEY);
        for (Entity illusion : illusions) {
            if (!(illusion instanceof IllusionHandler illusionRendering)) throw NOT_ILLUSIONABLE.create();
            illusionRendering.illusionable$clearIllusionTargets();
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearIllusionTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> illusions = EntityArgumentType.getEntities(context, ILLUSION_KEY);
        Collection<? extends Entity> victims = EntityArgumentType.getEntities(context, VICTIMS_KEY);
        for (Entity illusion : illusions) {
            if (!(illusion instanceof IllusionHandler illusionRendering)) throw NOT_ILLUSIONABLE.create();
            illusionRendering.illusionable$modifyIllusionTargets(uuids -> uuids.removeAll(victims.stream().map(Entity::getUuid).toList()));
        }
        return Command.SINGLE_SUCCESS;
    }
}
