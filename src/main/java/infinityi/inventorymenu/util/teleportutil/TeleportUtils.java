package infinityi.inventorymenu.util.teleportutil;

import java.util.Collections;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class TeleportUtils {

    public static void teleport(ServerPlayer player, Vec3 target, ServerLevel world){
        if (player.level() == null) return;
        player.level().getServer().execute(() -> player.teleportTo(world, target.x , target.y, target.z, Collections.emptySet(), player.yHeadRot, player.getXRot(), true));
    }

}
