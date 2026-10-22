package infinityi.inventorymenu.dataparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import infinityi.inventorymenu.InventoryMenu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File("config/inventory-menu.json");
    private static volatile ModConfig config = new ModConfig();

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            config = new ModConfig();
            saveConfig();
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                config = (loaded != null) ? loaded : new ModConfig();
            } catch (Exception e) {
                InventoryMenu.LOGGER.error("Failed to load inventory-menu configuration, using default config", e);
                config = new ModConfig();
            }
        }
    }

    public static void saveConfig() {
        try {
            File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (config == null) {
                config = new ModConfig();
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException e) {
            InventoryMenu.LOGGER.error("Failed to save inventory-menu configuration", e);
        }
    }

    public static ModConfig getConfig() {
        if (config == null) {
            config = new ModConfig();
        }
        return config;
    }
}
