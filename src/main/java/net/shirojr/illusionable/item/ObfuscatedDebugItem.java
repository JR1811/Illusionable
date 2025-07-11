package net.shirojr.illusionable.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
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
        if (toggleObfuscation(world, user, user, stack)) return TypedActionResult.success(stack, world.isClient());
        return TypedActionResult.pass(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (toggleObfuscation(user.getWorld(), user, entity, stack)) {
            return ActionResult.success(user.getWorld().isClient());
        }
        return ActionResult.PASS;
    }

    public static boolean toggleObfuscation(World world, PlayerEntity user, LivingEntity target, ItemStack stack) {
        if (user.getItemCooldownManager().isCoolingDown(stack.getItem())) return false;
        if (world instanceof ServerWorld serverWorld) {
            if (!target.hasStatusEffect(IllusionableStatusEffects.OBFUSCATED.value())) {
                target.removeStatusEffect(IllusionableStatusEffects.OBFUSCATED.value());
                target.addStatusEffect(new StatusEffectInstance(IllusionableStatusEffects.OBFUSCATED.value(), 20 * 60 * 5));
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.PLAYERS, 2f, 1f);
                user.getItemCooldownManager().set(stack.getItem(), 100);
            } else {
                target.removeStatusEffect(IllusionableStatusEffects.OBFUSCATED.value());
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.PLAYERS, 2f, 1f);
                user.getItemCooldownManager().set(stack.getItem(), 100);
            }
        }
        return true;
    }
}
