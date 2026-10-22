package infinityi.inventorymenu.dataparser;

import infinityi.inventorymenu.menu.MenuLayout;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    public final Map<UUID, MenuLayout> currentMenu = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastClickTimes = new ConcurrentHashMap<>();

    public void set(ServerPlayer player, MenuLayout layout) {
        if (player == null) return;
        if (layout == null) {
            currentMenu.remove(player.getUUID());
        } else {
            currentMenu.put(player.getUUID(), layout);
        }
    }

    public MenuLayout get(ServerPlayer player) {
        return player != null ? currentMenu.get(player.getUUID()) : null;
    }

    public void remove(UUID uuid) {
        if (uuid != null) {
            currentMenu.remove(uuid);
            lastClickTimes.remove(uuid);
        }
    }

    public void remove(ServerPlayer player) {
        if (player != null) {
            remove(player.getUUID());
        }
    }

    public boolean canClick(ServerPlayer player, long debounceMs) {
        if (player == null) return false;
        long now = System.currentTimeMillis();
        UUID uuid = player.getUUID();
        Long last = lastClickTimes.get(uuid);
        if (last != null && (now - last) < debounceMs) {
            return false;
        }
        lastClickTimes.put(uuid, now);
        return true;
    }
}
