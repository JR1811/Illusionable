package net.shirojr.illusionable.cca.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.IllusionableComponents;
import net.shirojr.illusionable.cca.util.IllusionStateCallback;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface IllusionComponent extends Component {
    Identifier KEY = Illusionable.getId("illusion");

    static IllusionComponent fromEntity(LivingEntity entity) {
        return IllusionableComponents.ILLUSION_DATA.get(entity);
    }

    LivingEntity getEntity();

    Collection<IllusionStateCallback> getListeners();

    boolean isIllusion();   //TODO: reverse, grab other entity on assigned and add it to their list

    void setIllusionState(boolean isIllusion, boolean sync);

    Set<UUID> getTargets();

    boolean isTargeting(LivingEntity other);

    void modifyTargets(Consumer<HashSet<UUID>> consumer, boolean sync);

    IconRendering getIconRendering();

    default void sync() {
        IllusionableComponents.ILLUSION_DATA.sync(getEntity());
    }

    class IconRendering {
        private boolean showIcon;
        private double scale;
        private double radius;

        public IconRendering(boolean showIcon, double scale, double radius) {
            this.showIcon = showIcon;
            this.scale = scale;
            this.radius = radius;
        }

        public boolean showIcons() {
            return showIcon;
        }

        public void setShowIcon(boolean showIcon) {
            this.showIcon = showIcon;
        }

        public double getScale() {
            return scale;
        }

        public boolean setScale(double scale) {
            if (scale <= 0) return false;
            this.scale = scale;
            return true;
        }

        public double getRadius() {
            return radius;
        }

        public boolean setRadius(double radius) {
            if (radius <= 0) return false;
            this.radius = radius;
            return true;
        }

        public void toNbt(NbtCompound nbt) {
            nbt.putBoolean("IconsVisible", this.showIcons());
            nbt.putDouble("IconsScale", this.getScale());
            nbt.putDouble("IconsRadius", this.getRadius());
        }

        @Nullable
        public static IconRendering fromNbt(NbtCompound nbt) {
            if (!nbt.contains("IconsVisible") || !nbt.contains("IconsScale") || !nbt.contains("IconsRadius")) {
                return null;
            }
            return new IconRendering(nbt.getBoolean("IconsVisible"), nbt.getDouble("IconsScale"), nbt.getDouble("IconsRadius"));
        }
    }
}
