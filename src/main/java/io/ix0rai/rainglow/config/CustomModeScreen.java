package io.ix0rai.rainglow.config;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceBooleanOption;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.ix0rai.rainglow.Rainglow;
import io.ix0rai.rainglow.data.SquidColour;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomModeScreen extends RainglowScreen {
    private final SpruceOption clearOption;
    private final SpruceOption saveOption;
    private final SpruceBooleanOption[] colourToggles = new SpruceBooleanOption[SquidColour.values().length];
    private final boolean[] toggleStates = new boolean[SquidColour.values().length];

    public CustomModeScreen(@Nullable Screen parent) {
        super(parent, Rainglow.translatableText("config.title"));

        // create toggles for each colour
        for (int i = 0; i < SquidColour.values().length; i ++) {
            final SquidColour colour = SquidColour.values()[i];
            final int index = i;

            toggleStates[index] = Rainglow.CONFIG.getCustom().contains(colour);

            colourToggles[index] = new SpruceBooleanOption(Rainglow.translatableTextKey("colour." + colour.getId()),
                    () -> toggleStates[index],
                    enable -> toggleStates[index] = enable,
                    null,
                    true
            );
        }

        // toggles all colours to false
        this.clearOption = SpruceSimpleActionOption.of(Rainglow.translatableTextKey("config.clear"),
            btn -> {
                for (int i = 0; i < SquidColour.values().length; i ++) {
                    toggleStates[i] = false;
                }

                MinecraftClient client = MinecraftClient.getInstance();
                this.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        });

        // writes all the toggled colours to the config and reloads custom mode
        this.saveOption = SpruceSimpleActionOption.of(Rainglow.translatableTextKey("config.save"),
                buttonWidget -> {
                    List<SquidColour> newCustom = new ArrayList<>();

                    for (int i = 0; i < SquidColour.values().length; i ++) {
                        if (toggleStates[i]) {
                            newCustom.add(SquidColour.values()[i]);
                        }
                    }

                    Rainglow.CONFIG.setCustom(newCustom, true);
                    this.onClose();
                }
        );
    }

    @Override
    protected void init() {
        super.init();

        // create a list of toggles for each colour
        SpruceOptionListWidget options = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - (35 + 22));
        for (int i = 0; i < SquidColour.values().length; i += 2) {
            SpruceOption secondToggle = null;
            if (i + 1 < SquidColour.values().length) {
                secondToggle = colourToggles[i + 1];
            }
            options.addOptionEntry(colourToggles[i], secondToggle);
        }
        this.addDrawableChild(options);

        // save and clear buttons
        this.addDrawableChild(this.clearOption.createWidget(Position.of(this, this.width / 2 - 155, this.height - 29), 150));
        this.addDrawableChild(this.saveOption.createWidget(Position.of(this, this.width / 2 - 155 + 160, this.height - 29), 150));
    }
}
