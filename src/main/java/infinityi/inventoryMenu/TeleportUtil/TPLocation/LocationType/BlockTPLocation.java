package infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType;

import com.mojang.serialization.Codec;
import infinityi.inventoryMenu.ItemAction.Actions.TeleportAction;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import infinityi.inventoryMenu.TeleportUtil.TeleportCost;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

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
        player.teleport(destinationWorld, location.getX() + 0.5, location.getY(), location.getZ() + 0.5, PositionFlag.DELTA, player.headYaw, player.lastPitch, false);
        player.closeHandledScreen();
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
