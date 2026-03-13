package infinityi.inventorymenu.util.teleportutil.tplocation.LocationType;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.TeleportUtils;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;


public record GlobalTPLocation(GlobalPos location) implements TPLocation {

    public static final Codec<GlobalTPLocation> CODEC = GlobalPos.CODEC.xmap(GlobalTPLocation::new, GlobalTPLocation::location);


    @Override
    public void teleport(ServerPlayer player, boolean safeCheck, TeleportCost cost) {
        ServerLevel destinationWorld = player.level().getServer().getLevel(location.dimension());
        if (destinationWorld == null) {
            player.sendSystemMessage(Component.translatable("§cThat world does not exist! %s", location.dimension().identifier().toString()));
            return;
        }
        if (safeCheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, location.pos())) {
                player.sendSystemMessage(Component.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        cost.applyCost(player, location.pos());
        player.closeContainer();
        TeleportUtils.teleport(player, location.pos().getCenter(), destinationWorld);
    }

    @Override
    public BlockPos getPos(MinecraftServer server) {
        return location.pos();
    }

    @Override
    public ServerPlayer getPlayer(MinecraftServer server) {
        return null;
    }

    @Override
    public Integer getDistance(ServerPlayer player) {
        if (player.level().dimension().equals(location.dimension())) {
            return TeleportAction.distanceBetween(player.blockPosition(), location.pos());
        }
        return TeleportAction.distanceBetween(player.blockPosition(), BlockPos.containing(0, 0, 0));
    }
}
