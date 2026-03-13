package infinityi.inventorymenu.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import infinityi.inventorymenu.menu.MenuLayout;
import java.util.Map;
import java.util.NavigableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record MenuNavigationAction(String navigate, Identifier destination) implements Action {
    public static final MapCodec<MenuNavigationAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("action").forGetter(MenuNavigationAction::navigate),
            Identifier.CODEC.optionalFieldOf("menu", Identifier.parse("")).forGetter(MenuNavigationAction::destination)
    ).apply(instance, MenuNavigationAction::new));

    @Override
    public void execute(ServerPlayer player) {
        MenuLayout layout = InventoryMenu.dataManager.playerData().currentMenu.get(player);
        switch (navigate) {
            case "close":
                player.closeContainer();
                break;
            case "open":
                InventoryMenu.dataManager.menus().getMenu(destination).ifPresentOrElse(menuLayout -> menuLayout.open(player), () -> {
                    player.sendSystemMessage(Component.literal("§cThis menu doesn't exist"));
                    player.closeContainer();
                });
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


    public void scroll_menu(ServerPlayer player, MenuLayout currentLayout, Boolean isNext) {
        String name = currentLayout.menu_group().getFirst();
        int currentIndex = currentLayout.menu_group().getSecond();
        NavigableMap<Integer, MenuLayout> groupMenu = InventoryMenu.dataManager.menus().getGroup(name);
        if (groupMenu.isEmpty() || groupMenu.size() == 1) return;
        Map.Entry<Integer, MenuLayout> targetEntry = isNext
                ? groupMenu.higherEntry(currentIndex)
                : groupMenu.lowerEntry(currentIndex);
        if (targetEntry == null) targetEntry = groupMenu.firstEntry();
        MenuLayout nextLayout = targetEntry.getValue();
        nextLayout.open(player);
    }
}
