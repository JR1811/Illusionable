package net.shirojr.illusionable.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class GlyphsParticle extends Particle {
    private final long startTime;

    public GlyphsParticle(ClientWorld world, Entity source, Entity target, long travelDuration) {
        super(world, source.getX(), source.getBodyY(0.5), source.getZ());
        this.startTime = world.getTime();
        this.maxAge = 2000;
        this.collidesWithWorld = false;
        this.gravityStrength = 0;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.age++;
        if (this.age >= this.maxAge) {
            this.markDead();
            return;
        }

    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {

    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
