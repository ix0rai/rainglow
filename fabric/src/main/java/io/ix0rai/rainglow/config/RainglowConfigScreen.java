package io.ix0rai.rainglow.config;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceCyclingOption;
import dev.lambdaurora.spruceui.option.SpruceOption;
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import io.ix0rai.rainglow.Rainglow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RainglowConfigScreen extends SpruceScreen {
    private final Screen parent;

    private final SpruceOption modeOption;
    private final SpruceOption resetOption;
    private RainglowMode mode;

    public RainglowConfigScreen(@Nullable Screen parent) {
        super(Rainglow.translatableText("config.title"));
        this.parent = parent;
        this.mode = RainglowConfig.getMode();

        this.modeOption = new SpruceCyclingOption(Rainglow.translatableTextKey("config.mode"),
                amount -> mode = mode.next(),
                option -> option.getDisplayText(mode.getTranslatedText()),
                Component.translatable(Rainglow.MOD_ID + ".tooltip.mode",
                        List.of(RainglowMode.values())
                )
        );

        this.resetOption = SpruceSimpleActionOption.reset(btn -> {
            this.mode = RainglowMode.RAINBOW;
            Minecraft client = Minecraft.getInstance();
            this.init(client, client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
        });
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        } else {
            super.onClose();
        }
    }

    @Override
    protected void init() {
        super.init();

        int buttonHeight = 20;

        SpruceOptionListWidget options = new SpruceOptionListWidget(Position.of(0, 22), this.width, this.height - (35 + 22));
        options.addOptionEntry(this.modeOption, null);
        this.addRenderableWidget(options);

        this.addRenderableWidget(this.resetOption.createWidget(Position.of(this, this.width / 2 - 155, this.height - 29), 150));
        this.addRenderableWidget(new SpruceButtonWidget(Position.of(this, this.width / 2 - 155 + 160, this.height - 29), 150,
                buttonHeight, Rainglow.translatableText("config.save"),
                buttonWidget -> {
                    this.onClose();
                    RainglowConfig.setMode(this.mode);
                }
        ));
    }
}
