package io.ix0rai.rainglow.mixin;

import io.ix0rai.rainglow.Rainglow;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Squid.class)
public abstract class SquidEntityMixin extends WaterAnimal {
    protected SquidEntityMixin(EntityType<? extends WaterAnimal> entityType, Level world) {
        super(entityType, world);
        throw new UnsupportedOperationException();
    }

    @Shadow protected abstract ParticleOptions getInkParticle();
    @Shadow protected abstract Vec3 rotateVector(Vec3 vec3d);
    @Shadow protected abstract SoundEvent getSquirtSound();

    /**
    * @author ix0rai
    * @reason pass custom colour index to spawnParticles
    */
    @Overwrite
    private void spawnInk() {
        // mostly copied from the original method
        this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
        Vec3 vec3d = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());

        for(int i = 0; i < 30; i ++) {
            Vec3 vec3d2 = this.rotateVector(new Vec3(this.random.nextFloat() * 0.6 - 0.3, -1.0, this.random.nextFloat() * 0.6 - 0.3));
            Vec3 vec3d3 = vec3d2.scale(0.3 + (this.random.nextFloat() * 2.0F));

            try {
                // send in custom colour data
                String colour = Rainglow.getColour(this.getEntityData(), this.random);
                int index = Rainglow.getColourIndex(colour);
                // round x to 1 decimal place and append index data to the next two
                ((ServerLevel) this.getLevel()).sendParticles(this.getInkParticle(), (Math.round(vec3d.x * 10)) / 10D + index / 1000D, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1);
            } catch (Exception e) {
                // if colour tracker data is not present do not try to send it
                // this behaviour will occur when a normal squid squirts
                ((ServerLevel) this.getLevel()).sendParticles(this.getInkParticle(), vec3d.x, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1);
            }
        }
    }
}
