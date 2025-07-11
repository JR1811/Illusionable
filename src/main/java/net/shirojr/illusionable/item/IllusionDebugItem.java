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
import net.shirojr.illusionable.cca.component.IllusionComponent;

public class IllusionDebugItem extends Item {
    public IllusionDebugItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world instanceof ServerWorld serverWorld) {
            IllusionComponent illusionComponent = IllusionComponent.fromEntity(user);
            illusionComponent.setIllusionState(!illusionComponent.isIllusion(), true);
            serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.PLAYERS, 2f, 1f);
            user.getItemCooldownManager().set(stack.getItem(), 100);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getWorld() instanceof ServerWorld serverWorld) {
            IllusionComponent illusionComponent = IllusionComponent.fromEntity(user);
            if (illusionComponent.getTargets().contains(entity.getUuid())) {
                illusionComponent.modifyTargets(uuids -> uuids.removeIf(uuid -> uuid.equals(entity.getUuid())), true);
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.PLAYERS, 2f, 1f);
            } else {
                illusionComponent.modifyTargets(uuids -> uuids.add(entity.getUuid()), true);
                serverWorld.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.PLAYERS, 2f, 1f);
            }
            user.getItemCooldownManager().set(stack.getItem(), 100);
        }
        return ActionResult.success(user.getWorld().isClient());
    }
}
