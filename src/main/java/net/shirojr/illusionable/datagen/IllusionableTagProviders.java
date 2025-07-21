package net.shirojr.illusionable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.illusionable.init.IllusionableDamageTypes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class IllusionableTagProviders {
    public static class DamageTypeTagProvider extends FabricTagProvider<DamageType> {
        public DamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            Map<TagKey<DamageType>, HashSet<IllusionableDamageTypes.DamageTypePair>> invertedMap = new HashMap<>();
            for (var entry : IllusionableDamageTypes.ALL_DAMAGE_TYPES.entrySet()) {
                for (TagKey<DamageType> tag : entry.getValue().tags()) {
                    invertedMap.computeIfAbsent(tag, damageTypeTagKey -> new HashSet<>()).add(entry.getValue());
                }
            }
            for (var entry : invertedMap.entrySet()) {
                FabricTagProvider<DamageType>.FabricTagBuilder builder = getOrCreateTagBuilder(entry.getKey()).setReplace(false);
                for (IllusionableDamageTypes.DamageTypePair damageTypePair : entry.getValue()) {
                    builder.addOptional(damageTypePair.get());
                }
            }
        }
    }

    public static void addAllProviders(FabricDataGenerator.Pack pack) {
        pack.addProvider(DamageTypeTagProvider::new);
    }
}
