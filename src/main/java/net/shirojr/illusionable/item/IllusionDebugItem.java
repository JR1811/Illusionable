package net.shirojr.illusionable.item;

import net.minecraft.entity.LivingEntity;
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
import net.shirojr.illusionable.util.wrapper.IllusionHandler;

public class IllusionDebugItem extends Item {
    public IllusionDebugItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof IllusionHandler illusion) {
            if (world instanceof ServerWorld serverWorld) {
                illusion.illusionable$setIllusion(!illusion.illusionable$isIllusion());
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.PLAYERS, 2f, 1f);
                user.getItemCooldownManager().set(stack.getItem(), 100);
            }
            return TypedActionResult.success(stack, world.isClient());
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user instanceof IllusionHandler illusion && entity instanceof IllusionHandler) {
            if (user.getWorld() instanceof ServerWorld serverWorld) {
                if (illusion.illusionable$getIllusionTargets().contains(entity)) {
                    illusion.illusionable$modifyIllusionTargets(uuids -> uuids.removeIf(uuid -> uuid.equals(entity.getUuid())), true);
                    serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.PLAYERS, 2f, 1f);
                } else {
                    illusion.illusionable$modifyIllusionTargets(uuids -> uuids.add(entity.getUuid()), true);
                    serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.PLAYERS, 2f, 1f);
                }
                user.getItemCooldownManager().set(stack.getItem(), 100);
            }
            return ActionResult.success(user.getWorld().isClient());
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
