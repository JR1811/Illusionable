package net.shirojr.illusionable.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.item.IllusionDebugItem;
import net.shirojr.illusionable.item.ObfuscatedDebugItem;

import java.util.ArrayList;
import java.util.List;

public interface IllusionableItems {
    List<ItemStack> ALL_ITEMS = new ArrayList<>();

    IllusionDebugItem ILLUSION_DEBUG = register("illusion_debug", new IllusionDebugItem(new Item.Settings().maxCount(1)));
    ObfuscatedDebugItem OBFUSCATED_DEBUG = register("obfuscated_debug", new ObfuscatedDebugItem(new Item.Settings().maxCount(1)));

    private static <T extends Item> T register(String name, T entry) {
        T registeredEntry = Registry.register(Registries.ITEM, Illusionable.getId(name), entry);
        ALL_ITEMS.add(new ItemStack(registeredEntry));
        return registeredEntry;
    }

    public static void initialize() {
        // static initialisation
    }
}
