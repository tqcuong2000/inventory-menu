package infinityi.inventorymenu.teleportutil.TeleportRequestManager;

import infinityi.inventorymenu.teleportutil.TeleportCost;
import net.minecraft.server.network.ServerPlayerEntity;

public record TeleportRequest(ServerPlayerEntity requester, long expiryTime, boolean safeCheck, TeleportCost cost) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
