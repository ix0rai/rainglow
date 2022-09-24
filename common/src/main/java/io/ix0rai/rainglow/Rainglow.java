package io.ix0rai.rainglow;

import io.ix0rai.rainglow.config.RainglowMode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.GlowSquid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rainglow {
    public static final String MOD_ID = "rainglow";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final EntityDataAccessor<String> COLOUR;

    private static final List<SquidColour> COLOURS = new ArrayList<>();
    // we maintain a hash map of textures as well to speed up lookup as much as possible
    private static final Map<String, ResourceLocation> TEXTURES = new HashMap<>();

    static {
        COLOUR = SynchedEntityData.defineId(GlowSquid.class, EntityDataSerializers.STRING);
        setMode(RainglowMode.RAINBOW);
    }

    public static void setMode(RainglowMode mode) {
        TEXTURES.clear();
        COLOURS.clear();
        mode.getColours().forEach(Rainglow::addColour);
    }

    private static void addColour(SquidColour colour) {
        COLOURS.add(colour);
        TEXTURES.put(colour.getId(), colour.getTexture());

        if (COLOURS.size() >= 100) {
            throw new RuntimeException("too many glow squid colours registered! only up to 99 are allowed");
        }
    }

    public static ResourceLocation getTexture(String colour) {
        return TEXTURES.get(colour);
    }

    public static int getColourIndex(String colour) {
        return COLOURS.indexOf(SquidColour.get(colour));
    }

    public static SquidColour.RGB getInkRgb(int index) {
        return COLOURS.get(index).getInkRgb();
    }

    public static SquidColour.RGB getPassiveParticleRGB(int index, RandomSource random) {
        SquidColour colour = COLOURS.get(index);
        return random.nextBoolean() ? colour.getPassiveParticleRgb() : colour.getAltPassiveParticleRgb();
    }

    public static SquidColour generateRandomColour(RandomSource random) {
        return COLOURS.get(random.nextInt(COLOURS.size()));
    }

    public static ResourceLocation getDefaultTexture() {
        return TEXTURES.get(COLOURS.get(0).getId());
    }

    public static String getColour(SynchedEntityData tracker, RandomSource random) {
        String colour = tracker.get(COLOUR);
        if (!isColourLoaded(colour)) {
            tracker.set(COLOUR, generateRandomColour(random).getId());
            colour = tracker.get(COLOUR);
        }

        return colour;
    }

    public static boolean isColourLoaded(String colour) {
        return COLOURS.contains(SquidColour.get(colour));
    }

    public static String translatableTextKey(String key) {
        return MOD_ID + "." + key;
    }

    public static Component translatableText(String key) {
        return Component.translatable(translatableTextKey(key));
    }
}
