package io.ix0rai.rainglow.mixin;

import io.ix0rai.rainglow.Rainglow;
import io.ix0rai.rainglow.SquidColour;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlowParticle.GlowSquidProvider.class)
public class GlowParticleMixin {
    @Final
    @Shadow
    private SpriteSet sprite;

    /**
     * @author ix0rai
     * @reason recolor particles
     */
    @Inject(method = "createParticle*", at = @At("HEAD"), cancellable = true)
    public void createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<GlowParticle> cir) {
        // we use whether g is over 100 to determine if we should override the method
        if (g > 99) {
            g -= 100;
            GlowParticle glowParticle = new GlowParticle(clientWorld, d, e, f, 0.5 - GlowParticle.RANDOM.nextDouble(), h, 0.5 - GlowParticle.RANDOM.nextDouble(), this.sprite);

            // we check the g value to see what the colour is
            SquidColour.RGB rgb = Rainglow.getPassiveParticleRGB((int) g, GlowParticle.RANDOM);
            glowParticle.setColor(rgb.r(), rgb.g(), rgb.b());

            glowParticle.yd *= 0.2;
            if (g == 0.0 && i == 0.0) {
                glowParticle.xd *= 0.1;
                glowParticle.zd *= 0.1;
            }

            glowParticle.setLifetime((int) (8.0 / (clientWorld.random.nextDouble() * 0.8 + 0.2)));
            cir.setReturnValue(glowParticle);
        }
    }
}
