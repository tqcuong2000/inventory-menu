package infinityi.inventorymenu.util.teleportutil.tplocation.LocationType;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.TeleportUtils;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public record BlockTPLocation(BlockPos location) implements TPLocation {

    public static final Codec<BlockTPLocation> CODEC = BlockPos.CODEC.xmap(BlockTPLocation::new, BlockTPLocation::location);

    @Override
    public void teleport(ServerPlayerEntity player, boolean safeCheck, TeleportCost cost) {
        if (player.getEntityWorld().getServer() == null) return;
        ServerWorld destinationWorld = player.getEntityWorld().getServer().getOverworld();
        if (safeCheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, location)) {
                player.sendMessage(Text.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        cost.applyCost(player, location);
        player.closeHandledScreen();
        TeleportUtils.teleport(player, location.toCenterPos(), destinationWorld);

    }

    @Override
    public BlockPos getPos(MinecraftServer server) {
        return location;
    }

    @Override
    public ServerPlayerEntity getPlayer(MinecraftServer server) {
        return null;
    }

    @Override
    public Integer getDistance(ServerPlayerEntity player) {
        return TeleportAction.distanceBetween(player.getBlockPos(), location);
    }

}
