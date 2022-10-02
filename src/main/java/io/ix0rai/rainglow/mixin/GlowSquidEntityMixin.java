package io.ix0rai.rainglow.mixin;

import io.ix0rai.rainglow.Rainglow;
import io.ix0rai.rainglow.SquidColour;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.ix0rai.rainglow.Rainglow.COLOUR;

@Mixin(GlowSquidEntity.class)
public abstract class GlowSquidEntityMixin extends SquidEntity {
    private static final String COLOUR_KEY = "Colour";

    protected GlowSquidEntityMixin(EntityType<? extends SquidEntity> entityType, World world) {
        super(entityType, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    protected void initDataTracker(CallbackInfo ci) {
        // generate random colour
        this.getDataTracker().startTracking(COLOUR, Rainglow.generateRandomColour(random).getId());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(COLOUR_KEY, this.getDataTracker().get(COLOUR));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        String colour = nbt.getString(COLOUR_KEY);
        // if read colour does not exist in the colour map, generate the squid a new one
        if (Rainglow.isColourLoaded(colour)) {
            this.getDataTracker().set(COLOUR, colour);
        } else {
            this.getDataTracker().set(COLOUR, Rainglow.generateRandomColour(random).getId());
        }
    }

    /**
     * @author ix0rai
     * @reason change particles based on colour
     */
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"), cancellable = true)
    public void tickMovement(CallbackInfo ci) {
        String colour = Rainglow.getColour(dataTracker, this.random);
        if (!colour.equals(SquidColour.BLUE.getId())) {
            // we add 100 to g to let the mixin know that we want to override the method
            this.world.addParticle(ParticleTypes.GLOW, this.getParticleX(0.6), this.getRandomBodyY(), this.getParticleZ(0.6), Rainglow.getColourIndex(colour) + 100, 0, 0);
            ci.cancel();
        }
    }
}
