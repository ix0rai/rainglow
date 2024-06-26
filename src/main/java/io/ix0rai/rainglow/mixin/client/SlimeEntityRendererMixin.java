package io.ix0rai.rainglow.mixin.client;

import io.ix0rai.rainglow.data.RainglowEntity;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeEntityRenderer.class)
public class SlimeEntityRendererMixin {
    @Inject(method = "getTexture*", at = @At("HEAD"), cancellable = true)
    public void getTexture(SlimeEntity entity, CallbackInfoReturnable<Identifier> cir) {
        RainglowEntity.SLIME.overrideTexture(entity, cir);
    }
}
