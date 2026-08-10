package net.shirojr.illusionable.init;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.command.argument.ViewArgumentType;

public class IllusionableArgumentTypes {
    static {
        register("view", ViewArgumentType.class, ConstantArgumentSerializer.of(ViewArgumentType::view));
    }

    @SuppressWarnings("SameParameterValue")
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void register(
            String name,
            Class<? extends A> clazz,
            ArgumentSerializer<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(Illusionable.getId(name), clazz, serializer);
    }

    public static void initialize() {
        // static initialisation
    }
}
