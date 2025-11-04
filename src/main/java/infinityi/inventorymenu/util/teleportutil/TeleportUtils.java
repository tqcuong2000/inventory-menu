package infinityi.inventorymenu.util.teleportutil;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;

public class TeleportUtils {

    public static void teleport(ServerPlayerEntity player, Vec3d target, ServerWorld world){
        if (player.getServer() == null) return;
        player.getWorld().getServer().execute(() -> player.teleport(world, target.x , target.y, target.z, Collections.emptySet(), player.headYaw, player.getPitch(), true));
    }

}
