package net.shirojr.illusionable.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.shirojr.illusionable.cca.component.DamageDistributionComponent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DamageDistributionCommand {
    private static final SimpleCommandExceptionType NOT_DAMAGEABLE =
            new SimpleCommandExceptionType(Text.literal("Entity can't receive damage"));
    private static final SimpleCommandExceptionType INVALID_DURATION =
            new SimpleCommandExceptionType(Text.literal("Invalid Duration. Either positive or -1 for infinite duration"));

    @SuppressWarnings("unused")
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("damage").requires(source -> source.hasPermissionLevel(2))
                .then(literal("distribution")
                        .then(argument("user", EntityArgumentType.entity())
                                .then(argument("targets", EntityArgumentType.entities())
                                        .then(argument("duration", LongArgumentType.longArg())
                                                .then(argument("range", DoubleArgumentType.doubleArg(0))
                                                        .executes(DamageDistributionCommand::initDistribution)
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int initDistribution(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (!(EntityArgumentType.getEntity(context, "user") instanceof LivingEntity user)) {
            throw EntityArgumentType.NOT_ALLOWED_EXCEPTION.create();
        }
        DamageDistributionComponent damageDistributionComponent = DamageDistributionComponent.fromEntity(user);
        List<LivingEntity> targets = new ArrayList<>();
        for (Entity entity : EntityArgumentType.getEntities(context, "targets")) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                throw NOT_DAMAGEABLE.create();
            }
            targets.add(livingEntity);
        }
        long duration = LongArgumentType.getLong(context, "duration");
        if (duration != -1 && duration < 0) {
            throw INVALID_DURATION.create();
        }
        double range = DoubleArgumentType.getDouble(context, "range");
        damageDistributionComponent.start(targets, duration, range);
        return Command.SINGLE_SUCCESS;
    }
}
