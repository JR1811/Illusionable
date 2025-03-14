package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;

@SuppressWarnings("SameParameterValue")
public interface IllusionableItemGroups {
    RegistryKey<ItemGroup> ILLUSIONABLE_ITEM_GROUP = registerItemGroup("boatism",
            Text.translatable("itemgroup.illusionable.illusionable"), new ItemStack(IllusionableItems.ILLUSION_DEBUG));

    private static RegistryKey<ItemGroup> registerItemGroup(String name, Text displayName, ItemStack displayItemStack) {
        ItemGroup group = FabricItemGroup.builder().icon(() -> displayItemStack).displayName(displayName).build();
        Identifier groupIdentifier = Illusionable.getId(name);
        Registry.register(Registries.ITEM_GROUP, groupIdentifier, group);
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, groupIdentifier);
    }

    private static void initializeItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ILLUSIONABLE_ITEM_GROUP).register(boatismEntries -> boatismEntries.addAll(IllusionableItems.ALL_ITEMS));
    }

    static void initialize() {
        initializeItemGroups();
    }
}
