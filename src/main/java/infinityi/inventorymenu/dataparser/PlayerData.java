package infinityi.inventorymenu.dataparser;

import infinityi.inventorymenu.menu.MenuLayout;
import java.util.HashMap;
import net.minecraft.server.level.ServerPlayer;

public class PlayerData {
    public final HashMap<ServerPlayer, MenuLayout> currentMenu = new HashMap<>();

    public void set(ServerPlayer player, MenuLayout layout) {
        currentMenu.put(player, layout);
    }
}
