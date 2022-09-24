package io.ix0rai.rainglow.mixin;

import io.ix0rai.rainglow.Rainglow;
import net.minecraft.client.renderer.entity.GlowSquidRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.GlowSquid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GlowSquidRenderer.class)
public class GlowSquidEntityRendererMixin {
    /**
     * @reason Use the colour from the entity's NBT data
     * @author ix0rai
     */
    @Overwrite
    public ResourceLocation getTextureLocation(GlowSquid glowSquidEntity) {
        ResourceLocation texture = Rainglow.getTexture(glowSquidEntity.getEntityData().get(Rainglow.COLOUR));
        return texture != null ? texture : Rainglow.getDefaultTexture();
    }
}
