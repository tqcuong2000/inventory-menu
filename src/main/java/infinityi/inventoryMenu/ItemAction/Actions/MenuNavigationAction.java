package infinityi.inventoryMenu.ItemAction.Actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.DataParser.MenuDataManager;
import infinityi.inventoryMenu.InventoryMenu;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.ActionType;
import infinityi.inventoryMenu.MenuLayout.Menu;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;

public record MenuNavigationAction(String navigate, String destination) implements Action {
    public static final MapCodec<MenuNavigationAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("navigate").forGetter(MenuNavigationAction::navigate),
            Codec.STRING.optionalFieldOf("destination").xmap(s -> s.orElse(""), Optional::ofNullable).forGetter(MenuNavigationAction::destination)
    ).apply(instance, MenuNavigationAction::new));

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
        switch (navigate) {
            case "close":
                player.closeHandledScreen();
                break;
            case "open":
                open_menu(player);
                break;
            case "next":
                scroll_menu(player, layout, true);
                break;
            case "previous":
                scroll_menu(player, layout, false);
                break;
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.NAVIGATE;
    }

    @Override
    public JsonElement getData() {
        return CODEC.codec().encode(this, JsonOps.INSTANCE, new JsonObject())
                .getOrThrow(error -> new IllegalStateException("Error encoding! " + error));
    }

    @Override
    public Map<String, String> placeholderData(ServerPlayerEntity player) {
        return Map.of();
    }

    public void open_menu(ServerPlayerEntity player) {
        Identifier menuId = Identifier.of(InventoryMenu.MOD_ID, "menu/" + this.destination + ".json");
        InventoryMenu.getDataManager().menus().getMenu(menuId).ifPresentOrElse(
                layout -> player.openHandledScreen(Menu.createMenu(layout)),
                () -> player.sendMessage(Text.translatable("§CMenu doesn't exist or loaded correctly: $s", menuId))
        );
    }

    public void scroll_menu(ServerPlayerEntity player, MenuLayout currentLayout, Boolean isNext) {
        String name = currentLayout.group().name();
        int currentIndex = currentLayout.group().index();
        NavigableMap<Integer, MenuLayout> groupMenu = InventoryMenu.getDataManager().menus().getMenu(name);
        if (groupMenu.isEmpty() || groupMenu.size() <= 1) return;
        Map.Entry<Integer, MenuLayout> targetEntry = isNext ? groupMenu.higherEntry(currentIndex) : groupMenu.lowerEntry(currentIndex);
        if (targetEntry == null) targetEntry = groupMenu.firstEntry();
        MenuLayout nextLayout = targetEntry.getValue();
        player.openHandledScreen(Menu.createMenu(nextLayout));
    }
}
