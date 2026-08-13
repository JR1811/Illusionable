package net.shirojr.illusionable.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.shirojr.illusionable.command.util.View;

public class PerspectiveArgumentType extends EnumArgumentType<View> {
    private PerspectiveArgumentType() {
        super(View.CODEC, View::values);
    }

    public static EnumArgumentType<View> perspective() {
        return new PerspectiveArgumentType();
    }

    public static View getPerspective(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, View.class);
    }
}