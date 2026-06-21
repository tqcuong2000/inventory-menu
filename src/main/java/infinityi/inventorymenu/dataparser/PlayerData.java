package infinityi.inventorymenu.dataparser;

import infinityi.inventorymenu.menu.MenuLayout;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class PlayerData {
    public final HashMap<ServerPlayer, MenuLayout> currentMenu = new HashMap<>();

    public void set(ServerPlayer player, MenuLayout layout) {
        currentMenu.put(player, layout);
    }
}
