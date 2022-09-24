package io.ix0rai.rainglow.config;

import io.ix0rai.rainglow.Rainglow;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RainglowConfig {
    private static String mode;
    private static final String CONFIG_FILE_NAME = "rainglow.toml";
    private static final Path CONFIG_FILE_PATH = Paths.get(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME).toUri());

    private static final String MODE_KEY = "mode";

    static {
        // default mode: rainbow
        String modeToSet = RainglowMode.RAINBOW.getName();

        try {
            // parse config file (badly)
            String content = Files.readString(CONFIG_FILE_PATH);
            String secondHalf = content.split(MODE_KEY + " = ")[1];

            String modeName = secondHalf.split("\"")[1].split("\"")[0];

            List<String> modeNames = new ArrayList<>();
            for (RainglowMode rainglowMode :RainglowMode.values()) {
                modeNames.add(rainglowMode.getName());
            }

            if (modeNames.contains(modeName)) {
                modeToSet = modeName;
            } else {
                Rainglow.LOGGER.warn("parsed mode from config did not exist; using default");
            }
        } catch (IOException e) {
            Rainglow.LOGGER.info("config file not found; creating new");
        } catch (ArrayIndexOutOfBoundsException e) {
            Rainglow.LOGGER.warn("failed to read config file, using default mode (rainbow) and creating new config file");
        } finally {
            // set mode and write to config file
            mode = modeToSet;
            writeMode(modeToSet);
        }
    }

    private static void writeMode(String mode) {
        try {
            Files.writeString(CONFIG_FILE_PATH, MODE_KEY + " = \"" + mode + "\"");
        } catch (IOException e) {
            Rainglow.LOGGER.warn("could not write to config file!");
        }
    }

    public static RainglowMode getMode() {
        return RainglowMode.byId(mode).orElse(RainglowMode.RAINBOW);
    }

    public static void setMode(RainglowMode newMode) {
        mode = newMode.getName();
        Rainglow.setMode(newMode);
        writeMode(newMode.getName());
    }
}