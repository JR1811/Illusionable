package net.shirojr.illusionable.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.shirojr.illusionable.command.util.View;

public class ViewArgumentType extends EnumArgumentType<View> {
    private ViewArgumentType() {
        super(View.CODEC, View::values);
    }

    public static EnumArgumentType<View> view() {
        return new ViewArgumentType();
    }

    public static View getView(CommandContext<ServerCommandSource> context, String id) {
        return context.getArgument(id, View.class);
    }
}