package infinityi.inventorymenu.dataparser;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MenuDataManager implements ResourceReloader  {

    private static final String MENUS_DIRECTORY = "menu";
    private final Map<Identifier, MenuLayout> loadedMenus = new HashMap<>();
    private final Map<String, Map<Integer, MenuLayout>> groupedMenus = new HashMap<>();

    public Optional<MenuLayout> getMenu(Identifier menuId) {
        return Optional.ofNullable(loadedMenus.get(menuId));
    }

    public NavigableMap<Integer, MenuLayout> getMenu(String groupName) {
        return new TreeMap<>(groupedMenus.getOrDefault(groupName, Collections.emptyMap()));
    }

    public Text getMenuName(Identifier menuId) {
        Optional<MenuLayout> menu = getMenu(menuId);
        if (menu.isEmpty()) return Text.empty();
        return menu.get().name();
    }

    public Set<Identifier> getIds() {
        return loadedMenus.keySet();
    }


    @Override
    public CompletableFuture<Void> reload(
            ResourceReloader.Store store,
            Executor prepareExecutor,
            ResourceReloader.Synchronizer reloadSynchronizer,
            Executor applyExecutor
    ) {
        ResourceManager manager = store.getResourceManager();

        return CompletableFuture.supplyAsync(() -> prepare(manager), prepareExecutor)
                .thenCompose(reloadSynchronizer::whenPrepared)
                .thenAcceptAsync(this::apply, applyExecutor);
    }

    protected Map<Identifier, MenuLayout> prepare(ResourceManager manager) {
        Map<Identifier, MenuLayout> preparedData = new HashMap<>();
        Map<Identifier, Resource> foundResources = manager.findResources(
                MENUS_DIRECTORY,
                path -> path.getPath().endsWith(".json"));

        for (Map.Entry<Identifier, Resource> entry : foundResources.entrySet()) {
            try (Reader reader = new InputStreamReader(entry.getValue().getInputStream())) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                MenuLayout layout = MenuLayout.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .getOrThrow(IllegalStateException::new);

                Identifier originalId = entry.getKey();
                String path = originalId.getPath();
                path = path.substring(0, path.lastIndexOf("."))
                        .replaceFirst("^menu/", "");

                Identifier menuId = Identifier.of(originalId.getNamespace(), path);
                preparedData.put(menuId, layout);
            } catch (Exception e) {
                InventoryMenu.LOGGER.error("Error while reading file resource: {}", entry.getKey(), e);
            }
        }
        return preparedData;
    }


    protected void apply(Map<Identifier, MenuLayout> prepared) {
        loadedMenus.clear();
        groupedMenus.clear();
        loadedMenus.putAll(prepared);
        for (MenuLayout layout : loadedMenus.values()) {
            Pair<String, Integer> group = layout.menu_group();
            if (group != null && !group.getFirst().isEmpty()) {
                groupedMenus.computeIfAbsent(group.getFirst(), k -> new TreeMap<>())
                        .put(group.getSecond(), layout);
            }
        }
    }

}
