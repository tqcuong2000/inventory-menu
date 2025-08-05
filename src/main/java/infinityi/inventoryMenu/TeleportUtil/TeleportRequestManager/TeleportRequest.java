package infinityi.inventoryMenu.TeleportUtil.TeleportRequestManager;

import net.minecraft.server.network.ServerPlayerEntity;

public record TeleportRequest(ServerPlayerEntity requester, long expiryTime, boolean safeCheck) {
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
