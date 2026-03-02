package net.shirojr.illusionable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.init.IllusionableItemGroups;
import net.shirojr.illusionable.init.IllusionableItems;

import java.nio.file.Path;
import java.util.Locale;

public class IllusionableLanguageProvider extends FabricLanguageProvider {
    public IllusionableLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(IllusionableItemGroups.ILLUSIONABLE_ITEM_GROUP, "Illusionable");
        for (ItemStack stack : IllusionableItems.ALL_ITEMS) {
            Identifier id = Registries.ITEM.getId(stack.getItem());

            StringBuilder cleanItemName = new StringBuilder();
            String[] split = id.getPath().split("_");
            for (int i = 0; i < split.length; i++) {
                String word = split[i];
                if (i > 0) {
                    cleanItemName.append(" ");
                }
                cleanItemName.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
                cleanItemName.append(word.substring(1));
            }
            builder.add(stack.getItem(), cleanItemName.toString());
        }

        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/%s/lang/en_us.existing.json".formatted(Illusionable.MOD_ID)).orElseThrow();
            builder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
