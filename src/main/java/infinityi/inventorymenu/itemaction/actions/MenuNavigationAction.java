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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;

public record MenuNavigationAction(String navigate, String destination) implements Action {
    public static final MapCodec<MenuNavigationAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("action").forGetter(MenuNavigationAction::navigate),
            Codec.STRING.optionalFieldOf("menu").xmap(s -> s.orElse(""), Optional::ofNullable).forGetter(MenuNavigationAction::destination)
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

    public void open_menu(ServerPlayerEntity player) {
        Identifier menuId = Identifier.of(InventoryMenu.MOD_ID, "menu/" + this.destination + ".json");
        InventoryMenu.getDataManager().menus().getMenu(menuId).ifPresentOrElse(
                layout -> player.openHandledScreen(Menu.createMenu(layout)),
                () -> player.sendMessage(Text.translatable("§CMenu doesn't exist or loaded correctly: $s", menuId))
        );
    }

    public void scroll_menu(ServerPlayerEntity player, MenuLayout currentLayout, Boolean isNext) {
        String name = currentLayout.menu_group().getFirst();
        int currentIndex = currentLayout.menu_group().getSecond();
        NavigableMap<Integer, MenuLayout> groupMenu = InventoryMenu.getDataManager().menus().getMenu(name);
        if (groupMenu.isEmpty() || groupMenu.size() == 1) return;
        Map.Entry<Integer, MenuLayout> targetEntry = isNext ? groupMenu.higherEntry(currentIndex) : groupMenu.lowerEntry(currentIndex);
        if (targetEntry == null) targetEntry = groupMenu.firstEntry();
        MenuLayout nextLayout = targetEntry.getValue();
        player.openHandledScreen(Menu.createMenu(nextLayout));
    }
}
