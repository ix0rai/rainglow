package io.ix0rai.rainglow.config;

import io.ix0rai.rainglow.Rainglow;
import net.fabricmc.api.ModInitializer;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        // reason: set proper mode on game start
        // common code defaults to rainbow
        Rainglow.setMode(RainglowConfig.getMode());
    }
}
