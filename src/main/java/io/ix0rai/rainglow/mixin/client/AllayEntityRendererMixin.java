package io.ix0rai.rainglow.mixin.client;

import io.ix0rai.rainglow.Rainglow;
import io.ix0rai.rainglow.data.RainglowColour;
import io.ix0rai.rainglow.data.RainglowEntity;
import net.minecraft.client.render.entity.AllayRenderer;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayRenderer.class)
public class AllayEntityRendererMixin {

    @Inject(method = "getTexture*", at = @At("HEAD"), cancellable = true)
    public void getTexture(AllayEntity allayEntity, CallbackInfoReturnable<Identifier> cir) {
        String colour = Rainglow.getColour(RainglowEntity.ALLAY, allayEntity.getDataTracker(), allayEntity.getRandom());

        // if the colour is blue we don't need to override the method
        // this optimises a tiny bit
        if (Rainglow.CONFIG.isEntityEnabled(RainglowEntity.ALLAY) && !colour.equals(RainglowColour.BLUE.getId())) {
            Identifier texture = Rainglow.getTexture(RainglowEntity.ALLAY, colour);
            cir.setReturnValue(texture != null ? texture : Rainglow.getDefaultTexture(RainglowEntity.ALLAY));
        }
    }
}
