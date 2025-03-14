package net.shirojr.illusionable.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.shirojr.illusionable.init.IllusionableStatusEffects;

public class ObfuscatedDebugItem extends Item {
    public ObfuscatedDebugItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            if (!user.hasStatusEffect(IllusionableStatusEffects.OBFUSCATED)) {
                user.addStatusEffect(new StatusEffectInstance(IllusionableStatusEffects.OBFUSCATED, 20 * 60 * 5));
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.PLAYERS, 2f, 1f);
                user.getItemCooldownManager().set(stack.getItem(), 100);
            } else {
                user.removeStatusEffect(IllusionableStatusEffects.OBFUSCATED);
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.PLAYERS, 2f, 1f);
                user.getItemCooldownManager().set(stack.getItem(), 100);
            }
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
