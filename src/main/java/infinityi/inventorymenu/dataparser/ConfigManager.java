package infinityi.inventorymenu.dataparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/inventory-menu.json");
    private static ModConfig config;

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            config = new ModConfig();
            saveConfig();
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException ignored) {
                config = new ModConfig();
            }
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException ignored) {

        }
    }

    public static ModConfig getConfig() {
        return config;
    }
}

