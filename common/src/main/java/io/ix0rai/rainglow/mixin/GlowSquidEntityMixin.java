package io.ix0rai.rainglow.mixin;

import io.ix0rai.rainglow.Rainglow;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.ix0rai.rainglow.Rainglow.COLOUR;

@Mixin(GlowSquid.class)
public abstract class GlowSquidEntityMixin extends Squid {
    private static final String COLOUR_KEY = "Colour";

    protected GlowSquidEntityMixin(EntityType<? extends Squid> entityType, Level world) {
        super(entityType, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void initDataTracker(CallbackInfo ci) {
        // generate random colour
        this.getEntityData().define(COLOUR, Rainglow.generateRandomColour(random).getId());
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void writeCustomDataToNbt(CompoundTag nbt, CallbackInfo ci) {
        nbt.putString(COLOUR_KEY, this.getEntityData().get(COLOUR));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readCustomDataFromNbt(CompoundTag nbt, CallbackInfo ci) {
        String colour = nbt.getString(COLOUR_KEY);
        // if read colour does not exist in the colour map, generate the squid a new one
        if (Rainglow.isColourLoaded(colour)) {
            this.getEntityData().set(COLOUR, colour);
        } else {
            this.getEntityData().set(COLOUR, Rainglow.generateRandomColour(random).getId());
        }
    }

    /**
     * @author ix0rai
     * @reason change particles based on colour
     */
    @Override
    @Overwrite
    public void aiStep() {
        super.aiStep();
        int i = this.getDarkTicksRemaining();
        if (i > 0) {
            this.setDarkTicks(i - 1);
        }

        String colour = Rainglow.getColour(this.getEntityData(), this.random);
        // we add 100 to g to let the mixin know that we want to override the method
        this.getLevel().addParticle(ParticleTypes.GLOW, this.getRandomX(0.6), this.getRandomY(), this.getRandomZ(0.6), Rainglow.getColourIndex(colour) + 100D, 0, 0);
    }

    @Shadow
    public int getDarkTicksRemaining() {
        throw new UnsupportedOperationException();
    }

    @Shadow
    private void setDarkTicks(int ticks) {
        throw new UnsupportedOperationException();
    }
}
