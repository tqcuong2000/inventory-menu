package infinityi.inventorymenu.util.teleportutil.tplocation.LocationType;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.TeleportUtils;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record BlockTPLocation(BlockPos location) implements TPLocation {

    public static final Codec<BlockTPLocation> CODEC = BlockPos.CODEC.xmap(BlockTPLocation::new, BlockTPLocation::location);

    @Override
    public void teleport(ServerPlayer player, boolean safeCheck, TeleportCost cost) {
        ServerLevel destinationWorld = player.level().getServer().overworld();
        if (safeCheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, location)) {
                player.sendSystemMessage(Component.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        cost.applyCost(player, location);
        player.closeContainer();
        TeleportUtils.teleport(player, location.getCenter(), destinationWorld);

    }

    @Override
    public BlockPos getPos(MinecraftServer server) {
        return location;
    }

    @Override
    public ServerPlayer getPlayer(MinecraftServer server) {
        return null;
    }

    @Override
    public Integer getDistance(ServerPlayer player) {
        return TeleportAction.distanceBetween(player.blockPosition(), location);
    }

}
