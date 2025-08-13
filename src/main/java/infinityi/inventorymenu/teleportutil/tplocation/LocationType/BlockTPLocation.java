package infinityi.inventorymenu.teleportutil.tplocation.LocationType;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.itemaction.actions.TeleportAction;
import infinityi.inventorymenu.teleportutil.TeleportCost;
import infinityi.inventorymenu.teleportutil.TeleportUtils;
import infinityi.inventorymenu.teleportutil.tplocation.TPLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;

import java.util.Collections;

public record BlockTPLocation(BlockPos location) implements TPLocation {

    public static final Codec<BlockTPLocation> CODEC = BlockPos.CODEC.xmap(BlockTPLocation::new, BlockTPLocation::location);

    @Override
    public void teleport(ServerPlayerEntity player, boolean safeCheck, TeleportCost cost) {
        if (player.getServer() == null) return;
        ServerWorld destinationWorld = player.getServer().getOverworld();
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
