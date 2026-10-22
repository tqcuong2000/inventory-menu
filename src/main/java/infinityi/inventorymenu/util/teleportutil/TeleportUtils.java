package infinityi.inventorymenu.util.teleportutil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;

public class TeleportUtils {

    public static void teleport(ServerPlayer player, Vec3 target, ServerLevel world) {
        if (player == null || player.level() == null || player.level().getServer() == null || world == null || target == null) return;
        player.level().getServer().execute(() -> player.teleportTo(world, target.x, target.y, target.z, Collections.emptySet(), player.yHeadRot, player.getXRot(), true));
    }

}
