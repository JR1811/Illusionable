package net.shirojr.illusionable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.component.IllusionComponent;

import java.util.*;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class IllusionCommand implements CommandRegistrationCallback {
    public static final String ILLUSION_KEY = "illusion", ILLUSION_STATE_KEY = "illusionState", VICTIMS_KEY = "victims";
    public static final String ICONS_VISIBLE_KEY = "IconsVisible", ICONS_SCALE = "IconsScale", ICONS_RADIUS = "IconsRadius";

    private static final SimpleCommandExceptionType NOT_ILLUSIONABLE =
            new SimpleCommandExceptionType(Text.literal("Not Illusionable"));
    private static final SimpleCommandExceptionType NO_VICTIMS_AVAILABLE =
            new SimpleCommandExceptionType(Text.literal("No entries in victims list were applicable"));
    private static final SimpleCommandExceptionType DATA_NOT_SET =
            new SimpleCommandExceptionType(Text.literal("Data couldn't be applied"));


    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("illusion").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument(ILLUSION_KEY, EntityArgumentType.entities())
                                .then(argument(ILLUSION_STATE_KEY, BoolArgumentType.bool())
                                        .executes(IllusionCommand::setIllusionSate)))
                )
                .then(literal("add")
                        .then(argument(ILLUSION_KEY, EntityArgumentType.entities())
                                .then(argument(VICTIMS_KEY, EntityArgumentType.entities())
                                        .executes(IllusionCommand::addIllusionTargets)))
                )
                .then(literal("remove")
                        .then(argument(ILLUSION_KEY, EntityArgumentType.entities())
                                .executes(IllusionCommand::clearAllIllusionTargets)
                                .then(argument(VICTIMS_KEY, EntityArgumentType.entities())
                                        .executes(IllusionCommand::clearIllusionTargets)))
                )
                .then(literal("info")
                        .then(argument("target", EntityArgumentType.entity())
                                .executes(IllusionCommand::getIllusionInfo)
                        )
                )
                .then(literal("icons")
                        .then(literal("visible")
                                .then(argument(ICONS_VISIBLE_KEY, BoolArgumentType.bool())
                                        .executes(IllusionCommand::showIcons)
                                )
                        )
                        .then(literal("scale")
                                .then(argument(ICONS_SCALE, DoubleArgumentType.doubleArg(0.00001))
                                        .executes(IllusionCommand::setIconsScale)
                                )
                        )
                        .then(literal("radius")
                                .then(argument(ICONS_RADIUS, DoubleArgumentType.doubleArg(0.00001))
                                        .executes(IllusionCommand::setIconsRadius)
                                )
                        )
                )
        );
    }

    private static int getIllusionInfo(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Entity illusion = EntityArgumentType.getEntity(context, "target");
        if (!(illusion instanceof LivingEntity livingEntity)) {
            throw NOT_ILLUSIONABLE.create();
        }
        IllusionComponent component = IllusionComponent.fromEntity(livingEntity);
        ServerWorld world = context.getSource().getWorld();
        StringBuilder output = new StringBuilder("Current Targets for ").append(illusion.getName().getString()).append(": ");
        for (UUID targetUuid : component.getTargets()) {
            Entity entity = world.getEntity(targetUuid);
            output.append(", ");
            if (entity == null) {
                output.append(targetUuid);
            } else {
                output.append(entity.getName().getString());
            }
        }
        context.getSource().sendFeedback(() -> Text.literal(output.toString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setIconsRadius(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double radius = DoubleArgumentType.getDouble(context, ICONS_RADIUS);
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) throw NOT_ILLUSIONABLE.create();
        IllusionComponent component = IllusionComponent.fromEntity(player);
        if (!component.getIconRendering().setRadius(radius)) {
            throw DATA_NOT_SET.create();
        }
        component.sync();
        context.getSource().sendFeedback(() -> Text.literal("Set Icons Radius: " + radius), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setIconsScale(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        double scale = DoubleArgumentType.getDouble(context, ICONS_SCALE);
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) throw NOT_ILLUSIONABLE.create();
        IllusionComponent component = IllusionComponent.fromEntity(player);
        if (!component.getIconRendering().setScale(scale)) {
            throw DATA_NOT_SET.create();
        }
        component.sync();
        context.getSource().sendFeedback(() -> Text.literal("Set Icons Scale: " + scale), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int showIcons(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        boolean visible = BoolArgumentType.getBool(context, ICONS_VISIBLE_KEY);
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) throw NOT_ILLUSIONABLE.create();
        IllusionComponent component = IllusionComponent.fromEntity(player);
        component.getIconRendering().setShowIcon(visible);
        component.sync();
        context.getSource().sendFeedback(() -> Text.literal("Show icons: " + visible), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int setIllusionSate(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<Entity> illusions = new ArrayList<>(EntityArgumentType.getEntities(context, ILLUSION_KEY));
        boolean isIllusion = BoolArgumentType.getBool(context, ILLUSION_STATE_KEY);
        for (Entity entry : illusions) {
            if (!(entry instanceof LivingEntity illusion)) {
                Illusionable.LOGGER.error("{} was not illusionable", entry, NOT_ILLUSIONABLE.create());
                continue;
            }
            IllusionComponent illusionComponent = IllusionComponent.fromEntity(illusion);
            illusionComponent.setIllusionState(isIllusion, true);
            if (!isIllusion) {
                illusionComponent.modifyTargets(HashSet::clear, true);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addIllusionTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<Entity> illusions = new ArrayList<>(EntityArgumentType.getEntities(context, ILLUSION_KEY));
        List<Entity> victims = new ArrayList<>(EntityArgumentType.getEntities(context, VICTIMS_KEY));
        if (victims.isEmpty()) throw NO_VICTIMS_AVAILABLE.create();

        for (Entity entry : illusions) {
            if (!(entry instanceof LivingEntity illusion)) {
                Illusionable.LOGGER.error("{} was not illusionable", entry, NOT_ILLUSIONABLE.create());
                continue;
            }
            IllusionComponent illusionComponent = IllusionComponent.fromEntity(illusion);
            illusionComponent.modifyTargets(uuids -> uuids.addAll(victims.stream().map(Entity::getUuid).toList()), true);

            StringBuilder sb = new StringBuilder("Added");
            victims.forEach(entity -> sb.append(" ").append(entity.getName().getString()));
            context.getSource().sendFeedback(() -> Text.literal(sb.toString()), true);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int clearAllIllusionTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> illusions = EntityArgumentType.getEntities(context, ILLUSION_KEY);
        context.getSource().sendFeedback(() -> Text.literal("Cleared Illusion Targets").formatted(Formatting.GOLD), true);
        ServerWorld world = context.getSource().getWorld();

        for (Entity entry : illusions) {
            if (!(entry instanceof LivingEntity illusion)) {
                Illusionable.LOGGER.error("{} was not illusionable", entry, NOT_ILLUSIONABLE.create());
                continue;
            }
            IllusionComponent illusionComponent = IllusionComponent.fromEntity(illusion);

            context.getSource().sendFeedback(() -> Text.literal("For %s:".formatted(entry.getName().getString())), true);
            StringBuilder sb = new StringBuilder();
            for (UUID targetUuid : illusionComponent.getTargets()) {
                Entity target = world.getEntity(targetUuid);
                if (target == null) continue;
                String targetName = target.getName().getString();
                sb.append(targetName).append(" ");
            }
            context.getSource().sendFeedback(() -> Text.literal(sb.toString()), true);

            illusionComponent.modifyTargets(HashSet::clear, true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearIllusionTargets(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<? extends Entity> illusions = EntityArgumentType.getEntities(context, ILLUSION_KEY);
        Collection<? extends Entity> victims = EntityArgumentType.getEntities(context, VICTIMS_KEY);
        context.getSource().sendFeedback(() -> Text.literal("Cleared Illusion Targets").formatted(Formatting.GOLD), true);
        for (Entity entry : illusions) {
            if (!(entry instanceof LivingEntity illusion)) {
                Illusionable.LOGGER.error("{} was not illusionable", entry, NOT_ILLUSIONABLE.create());
                continue;
            }
            context.getSource().sendFeedback(() -> Text.literal("For %s:".formatted(entry.getName().getString())), true);
            StringBuilder sb = new StringBuilder();
            victims.forEach(entity -> sb.append(entity.getName().getString()).append(" "));
            context.getSource().sendFeedback(() -> Text.literal(sb.toString()), true);

            IllusionComponent illusionComponent = IllusionComponent.fromEntity(illusion);
            illusionComponent.modifyTargets(uuids -> victims.stream().map(Entity::getUuid).toList().forEach(uuids::remove), true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
