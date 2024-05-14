package io.ix0rai.rainglow.config;

import io.ix0rai.rainglow.Rainglow;
import io.ix0rai.rainglow.data.RainglowColour;
import io.ix0rai.rainglow.data.RainglowEntity;
import io.ix0rai.rainglow.data.RainglowMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.gui.widget.button.CyclingButtonWidget;
import net.minecraft.client.gui.widget.layout.GridWidget;
import net.minecraft.client.gui.widget.layout.HeaderFooterLayoutWidget;
import net.minecraft.client.gui.widget.layout.LayoutSettings;
import net.minecraft.client.gui.widget.layout.LinearLayoutWidget;
import net.minecraft.client.gui.widget.text.TextWidget;
import net.minecraft.client.option.Option;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// todo: now that config uses overrides, edit lock doesn't matter

public class RainglowConfigScreen extends Screen {
    private static final Text TITLE = Rainglow.translatableText("config.title");

    private final Screen parent;
    private final Map<RainglowEntity, DeferredSaveOption<Boolean>> toggles = new HashMap<>();
    private final Map<RainglowEntity, DeferredSaveOption<Integer>> sliders = new HashMap<>();

    private RainglowMode mode;
    private boolean isConfirming;

    public RainglowConfigScreen(@Nullable Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.mode = RainglowMode.get(Rainglow.CONFIG.mode.value());
    }

    @Override
    public void init() {
        HeaderFooterLayoutWidget headerFooterWidget = new HeaderFooterLayoutWidget(this, 61, 33);

        if (!this.isConfirming) {
            // header
            LinearLayoutWidget linearLayoutWidget = headerFooterWidget.addToHeader(LinearLayoutWidget.createVertical().setSpacing(20));
            linearLayoutWidget.add(new TextWidget(TITLE, this.textRenderer), LayoutSettings::alignHorizontallyCenter);
            linearLayoutWidget.add(createModeButton());

            // contents
            GridWidget gridWidget = new GridWidget();
            gridWidget.getDefaultSettings().setHorizontalPadding(4).setBottomPadding(4).alignHorizontallyCenter();
            GridWidget.AdditionHelper miscAdditionHelper = gridWidget.createAdditionHelper(2);
            for (RainglowEntity entity : RainglowEntity.values()) {
                DeferredSaveOption<Boolean> entityToggle = createEntityToggle(entity);
                miscAdditionHelper.add(entityToggle.createButton(MinecraftClient.getInstance().options));
                entityToggle.set(entityToggle.deferredValue);

                miscAdditionHelper.add(createColourRaritySlider(entity).createButton(MinecraftClient.getInstance().options));
            }

            headerFooterWidget.addToContents(gridWidget);

            // footer
            LinearLayoutWidget linearLayout = headerFooterWidget.addToFooter(LinearLayoutWidget.createHorizontal().setSpacing(8));
            linearLayout.add(ButtonWidget.builder(CommonTexts.DONE, button -> this.closeScreen()).build());
            linearLayout.add(ButtonWidget.builder(Rainglow.translatableText("config.save"), button -> {
                this.save();
                this.closeScreen(true);
            }).build());
        } else {
            LinearLayoutWidget titleWidget = headerFooterWidget.addToHeader(LinearLayoutWidget.createVertical().setSpacing(20));
            titleWidget.add(new TextWidget(this.title, this.textRenderer), LayoutSettings::alignHorizontallyCenter);

            LinearLayoutWidget contentWidget = headerFooterWidget.addToContents(new LinearLayoutWidget(250, 100, LinearLayoutWidget.Orientation.VERTICAL).setSpacing(8));
            contentWidget.add(new TextWidget(Rainglow.translatableText("config.unsaved_warning"), this.textRenderer), LayoutSettings::alignHorizontallyCenter);

            LinearLayoutWidget buttons = new LinearLayoutWidget(250, 20, LinearLayoutWidget.Orientation.HORIZONTAL);
            buttons.add(ButtonWidget.builder(Rainglow.translatableText("config.continue_editing"), (buttonWidget) -> {
                this.isConfirming = false;
                this.clearAndInit();
            }).build());
            buttons.add(ButtonWidget.builder(CommonTexts.YES, (buttonWidget) -> MinecraftClient.getInstance().setScreen(this.parent)).build());

            contentWidget.add(buttons, LayoutSettings::alignHorizontallyCenter);
        }

        headerFooterWidget.visitWidgets(this::addDrawableSelectableElement);
        headerFooterWidget.arrangeElements();
    }

    private DeferredSaveOption<Boolean> createEntityToggle(RainglowEntity entity) {
        return toggles.computeIfAbsent(entity, e -> DeferredSaveOption.createDeferredBoolean(
            "enable_" + e.getId(),
            "tooltip.entity_toggle",
            Rainglow.CONFIG.isEntityEnabled(e),
            enabled -> Rainglow.CONFIG.setEntityEnabled(e, enabled)
        ));
    }

    private DeferredSaveOption<Integer> createColourRaritySlider(RainglowEntity entity) {
        return sliders.computeIfAbsent(entity, e -> DeferredSaveOption.createDeferredRangedInt(
				e.getId() + "_rarity",
				"tooltip.rarity",
				Rainglow.CONFIG.getRarity(e),
				0,
				100,
				rarity -> Rainglow.CONFIG.setRarity(e, rarity)
		));
    }

    public CyclingButtonWidget<RainglowMode> createModeButton() {
        return CyclingButtonWidget.builder(RainglowMode::getText)
                .values(RainglowMode.values())
                .initially(this.mode)
                .tooltip(this::createColourListLabel)
                .build(
                        0,
                        0,
                        300,
                        20,
                        Rainglow.translatableText("config.mode"),
                        (cyclingButtonWidget, mode) -> {
                            RainglowConfigScreen.this.mode = mode;
                        }
                );
    }

    private void save() {
        if (Rainglow.CONFIG.isEditLocked(MinecraftClient.getInstance())) {
            sendConfigLockedToast();
        } else {
            Collection<Option<?>> options = new ArrayList<>(this.sliders.values());
            options.addAll(this.toggles.values());

            for (Option<?> option : options) {
                if (option instanceof DeferredSaveOption) {
                    ((DeferredSaveOption<?>) option).save();
                }
            }

            Rainglow.CONFIG.mode.setValue(this.mode.getId());
        }
    }

    private Tooltip createColourListLabel(RainglowMode mode) {
        // creates a label and appends all the colours that will be applied in the given mode
        StringBuilder text = new StringBuilder(Language.getInstance().get(Rainglow.translatableTextKey("config.colours_to_apply")));
        int maxDisplayedColourCount = 16;
        int maxColoursPerLine = 4;

        for (int i = 0; i < mode.getColours().size(); i += maxColoursPerLine) {
            if (i < maxDisplayedColourCount) {
                text.append("\n");

                int coloursLeft = mode.getColours().size() - i;
                int coloursToDisplay = Math.min(coloursLeft, maxColoursPerLine);

                for (int j = 0; j < coloursToDisplay; j++) {
                    RainglowColour currentColour = mode.getColours().get(i + j);
                    text.append(Language.getInstance().get(Rainglow.translatableTextKey("colour." + currentColour.getId())));
                    if (j < coloursToDisplay - 1) {
                        text.append(", ");
                    }
                }
            } else  {
                text.append("\n... ").append(mode.getColours().size() - maxDisplayedColourCount).append(" ").append(Language.getInstance().get(Rainglow.translatableTextKey("config.more")));
            }
        }

        // set colour to the mode's text colour
        Style style = Style.EMPTY.withColor(mode.getText().getStyle().getColor());
        return Tooltip.create(Text.literal(text.toString()).setStyle(style));
    }

    @Override
    public void closeScreen() {
        this.closeScreen(false);
    }

    public void closeScreen(boolean saved) {
        if (!saved) {
            this.isConfirming = true;
            this.clearAndInit();
        } else {
            MinecraftClient.getInstance().setScreen(this.parent);
        }
    }

    private static void sendConfigLockedToast() {
        Toast toast = new SystemToast(SystemToast.Id.PACK_LOAD_FAILURE, Rainglow.translatableText("config.server_locked_title"), Rainglow.translatableText("config.server_locked_description"));
        MinecraftClient.getInstance().getToastManager().add(toast);
    }
}
