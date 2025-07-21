package net.shirojr.illusionable;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.shirojr.illusionable.datagen.IllusionableDamageTypeProvider;
import net.shirojr.illusionable.datagen.IllusionableTagProviders;
import net.shirojr.illusionable.init.IllusionableDamageTypes;

public class IllusionableDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(IllusionableDamageTypeProvider::new);

		IllusionableTagProviders.addAllProviders(pack);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
		registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, IllusionableDamageTypes::bootstrap);
	}
}
