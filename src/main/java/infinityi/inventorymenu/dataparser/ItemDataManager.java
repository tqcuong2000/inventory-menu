package infinityi.inventorymenu.dataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemDataManager extends SinglePreparationResourceReloader<Map<Identifier, MenuItem>> implements IdentifiableResourceReloadListener {
    private static final String MENU_ITEMS_DIR = "menu-item";
    private static final Map<Identifier, MenuItem> loadItems = new HashMap<>();

    @Override
    protected Map<Identifier,MenuItem> prepare(ResourceManager manager, Profiler profiler) {
        Map<Identifier, MenuItem> preparedData = new HashMap<>();
        Map<Identifier, Resource> foundResources = manager.findResources(
                MENU_ITEMS_DIR,
                path -> path.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : foundResources.entrySet()) {
            try (Reader reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                MenuItem element = MenuItem.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .getOrThrow(IllegalStateException::new);
                String path = entry.getKey().toString();
                path = path.substring(0, path.lastIndexOf(".")).replaceFirst("menu-item/", "");
                preparedData.put(Identifier.of(path), element);
            } catch (Exception e) {
                InventoryMenu.LOGGER.error("Error while reading file resource: {}", entry.getKey(), e);
            }
        }
        return preparedData;
    }

    @Override
    protected void apply(Map<Identifier, MenuItem> prepared, ResourceManager manager, Profiler profiler) {
        loadItems.clear();
        loadItems.putAll(prepared);
        if (loadItems.isEmpty()) return;
        InventoryMenu.LOGGER.info("Successfully loaded {} menu item.", loadItems.size());
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(InventoryMenu.MOD_ID, "item_data_manager");
    }

    public Optional<MenuItem> getItem(Identifier identifier){
        return Optional.ofNullable(loadItems.get(identifier));
    }
}
