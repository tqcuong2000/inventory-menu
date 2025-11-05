package infinityi.inventorymenu.dataparser;

import infinityi.inventorymenu.menu.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

public class PlayerData {
    public final HashMap<ServerPlayerEntity, MenuLayout> currentMenu = new HashMap<>();

    public void set(ServerPlayerEntity player, MenuLayout layout) {
        currentMenu.put(player, layout);
    }
}
