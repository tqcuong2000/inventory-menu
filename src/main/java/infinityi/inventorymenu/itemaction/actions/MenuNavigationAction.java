package infinityi.inventorymenu.itemaction.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.itemaction.ActionType;
import infinityi.inventorymenu.menulayout.Menu;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.NavigableMap;

public record MenuNavigationAction(String navigate, Identifier destination) implements Action {
    public static final MapCodec<MenuNavigationAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("action").forGetter(MenuNavigationAction::navigate),
            Identifier.CODEC.optionalFieldOf("menu", Identifier.of("")).forGetter(MenuNavigationAction::destination)
    ).apply(instance, MenuNavigationAction::new));

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
        switch (navigate) {
            case "close":
                player.closeHandledScreen();
                break;
            case "open":
                Menu.open(destination, player);
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


    public void scroll_menu(ServerPlayerEntity player, MenuLayout currentLayout, Boolean isNext) {
        String name = currentLayout.menu_group().getFirst();
        int currentIndex = currentLayout.menu_group().getSecond();
        NavigableMap<Integer, MenuLayout> groupMenu = InventoryMenu.getDataManager().menus().getMenu(name);
        if (groupMenu.isEmpty() || groupMenu.size() == 1) return;
        Map.Entry<Integer, MenuLayout> targetEntry = isNext
                ? groupMenu.higherEntry(currentIndex)
                : groupMenu.lowerEntry(currentIndex);
        if (targetEntry == null) targetEntry = groupMenu.firstEntry();
        MenuLayout nextLayout = targetEntry.getValue();
        player.openHandledScreen(Menu.createMenu(nextLayout));
    }
}
