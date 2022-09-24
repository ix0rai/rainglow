package io.ix0rai.rainglow.config;

import io.ix0rai.rainglow.Rainglow;
import io.ix0rai.rainglow.SquidColour;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum RainglowMode {
    RAINBOW(Rainglow.translatableText("mode.rainbow"), ChatFormatting.LIGHT_PURPLE,
            SquidColour.RED,
            SquidColour.ORANGE,
            SquidColour.YELLOW,
            SquidColour.GREEN,
            SquidColour.BLUE,
            SquidColour.INDIGO,
            SquidColour.PURPLE
    ),
    ALL_COLOURS(Rainglow.translatableText("mode.all_colours"), ChatFormatting.GREEN,
            SquidColour.values()
    ),
    TRANS_PRIDE(Rainglow.translatableText("mode.trans_pride"), ChatFormatting.AQUA,
            SquidColour.BLUE,
            SquidColour.WHITE,
            SquidColour.PINK
    ),
    LESBIAN_PRIDE(Rainglow.translatableText("mode.lesbian_pride"), ChatFormatting.RED,
            SquidColour.RED,
            SquidColour.ORANGE,
            SquidColour.WHITE,
            SquidColour.PINK,
            SquidColour.PURPLE
    ),
    BI_PRIDE(Rainglow.translatableText("mode.bi_pride"), ChatFormatting.BLUE,
            SquidColour.BLUE,
            SquidColour.PINK,
            SquidColour.PURPLE
    ),
    GAY_PRIDE(Rainglow.translatableText("mode.gay_pride"), ChatFormatting.DARK_AQUA,
            SquidColour.BLUE,
            SquidColour.GREEN,
            SquidColour.WHITE
    ),
    PAN_PRIDE(Rainglow.translatableText("mode.pan_pride"), ChatFormatting.GOLD,
            SquidColour.PINK,
            SquidColour.YELLOW,
            SquidColour.BLUE
    ),
    ACE_PRIDE(Rainglow.translatableText("mode.ace_pride"), ChatFormatting.DARK_GRAY,
            SquidColour.BLACK,
            SquidColour.GRAY,
            SquidColour.WHITE,
            SquidColour.PURPLE
    ),
    ENBY_PRIDE(Rainglow.translatableText("mode.enby_pride"), ChatFormatting.DARK_PURPLE,
            SquidColour.YELLOW,
            SquidColour.WHITE,
            SquidColour.BLACK,
            SquidColour.PURPLE
    );

    private final Component text;
    private final List<SquidColour> colours;

    RainglowMode(Component text, ChatFormatting formatting, SquidColour... colours) {
        this.colours = List.of(colours);
        this.text = text.copy().withStyle(formatting);
    }

    public List<SquidColour> getColours() {
        return this.colours;
    }

    public RainglowMode next() {
        if (values().length == this.ordinal() + 1) {
            return values()[0];
        }
        return values()[this.ordinal() + 1];
    }

    public Component getTranslatedText() {
        return this.text;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public static Optional<RainglowMode> byId(String id) {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
