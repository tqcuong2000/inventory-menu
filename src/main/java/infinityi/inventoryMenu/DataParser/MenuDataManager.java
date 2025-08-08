package infinityi.inventoryMenu.DataParser;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import infinityi.inventoryMenu.InventoryMenu;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class MenuDataManager extends SinglePreparationResourceReloader<Map<Identifier, MenuLayout>> implements IdentifiableResourceReloadListener {

    private static final String MENUS_DIRECTORY = "menu";
    private final Map<Identifier, MenuLayout> loadedMenus = new HashMap<>();
    private final Map<String, Map<Integer, MenuLayout>> groupedMenus = new HashMap<>();

    public Optional<MenuLayout> getMenu(Identifier menuId) {
        return Optional.ofNullable(loadedMenus.get(menuId));
    }

    public Optional<MenuLayout> getMenuById(String id){
        return loadedMenus.values().stream()
                .filter(layout -> layout.id().equals(id))
                .findFirst();
    }

    public NavigableMap<Integer, MenuLayout> getMenu(String groupName) {
        return new TreeMap<>(groupedMenus.getOrDefault(groupName, Collections.emptyMap()));
    }

    public Text getMenuName(Identifier menuId){
        Optional<MenuLayout>  menu = getMenu(menuId);
        if (menu.isEmpty()) return  Text.empty();
        return menu.get().name();
    }

    public Set<Identifier> getLoadedMenuIds() {
        return loadedMenus.keySet();
    }

    public List<String> getIds(){
        return loadedMenus.values().stream().map(MenuLayout::id).collect(Collectors.toList());
    }

    @Override
    protected Map<Identifier, MenuLayout> prepare(ResourceManager manager, Profiler profiler) {
        Map<Identifier, MenuLayout> preparedData = new HashMap<>();
        Map<Identifier, Resource> foundResources = manager.findResources(MENUS_DIRECTORY, path -> path.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : foundResources.entrySet()) {
            try (Reader reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                MenuLayout layout = MenuLayout.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .getOrThrow(error -> new IllegalStateException("Cannot analyze " + entry.getKey() + ": " + error));
                preparedData.put(entry.getKey(), layout);
            } catch (Exception e) {
                InventoryMenu.LOGGER.error("Error while reading file resource: " + entry.getKey(), e);
            }
        }
        return preparedData;
    }

    @Override
    protected void apply(Map<Identifier, MenuLayout> prepared, ResourceManager manager, Profiler profiler) {
        loadedMenus.clear();
        groupedMenus.clear();
        loadedMenus.putAll(prepared);
        for (MenuLayout layout : loadedMenus.values()) {
            Pair<String,Integer> group = layout.menu_group();
            if (group != null && !group.getFirst().isEmpty()) {
                groupedMenus.computeIfAbsent(group.getFirst(), k -> new TreeMap<>()).put(group.getSecond(), layout);
            }
        }
        InventoryMenu.LOGGER.info("Successfully loaded {} menu layout.", loadedMenus.size());
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(InventoryMenu.MOD_ID, "menu_data_manager");
    }
}
