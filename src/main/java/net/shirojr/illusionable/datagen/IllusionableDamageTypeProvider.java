package net.shirojr.illusionable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.shirojr.illusionable.init.IllusionableDamageTypes;

import java.util.concurrent.CompletableFuture;

public class IllusionableDamageTypeProvider extends FabricDynamicRegistryProvider {
    public IllusionableDamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.add(registries.getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE), IllusionableDamageTypes.LINKED_DAMAGE.get());
    }

    @Override
    public String getName() {
        return "Illusionable Custom Damage Types";
    }
}
