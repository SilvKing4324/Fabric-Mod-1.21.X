package net.silvking432.silvkingsmod.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BlackHoleParticle extends SpriteBillboardParticle {

    public BlackHoleParticle(ClientWorld clientWorld, double x, double y, double z,
                                  SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);

        this.velocityMultiplier = 0.9f;

        this.maxAge = 300;
        this.setSpriteForAge(spriteProvider);
        this.red = 0.0f;
        this.green = 0.0f;
        this.blue = 0.0f;
        this.scale = 3.0f;
    }

    @Override
    public void tick() {
        this.prevAngle = this.angle;
        super.tick();


        this.angle += 0.15f;

        if (this.age > this.maxAge - 20) {
            this.scale *= 0.9f;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new BlackHoleParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}
