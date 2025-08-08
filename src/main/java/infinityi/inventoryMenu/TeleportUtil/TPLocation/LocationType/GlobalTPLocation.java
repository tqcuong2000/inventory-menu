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
import net.minecraft.util.math.GlobalPos;

public record GlobalTPLocation(GlobalPos location) implements TPLocation {

    public static final Codec<GlobalTPLocation> CODEC = GlobalPos.CODEC.xmap(GlobalTPLocation::new,GlobalTPLocation::location);


    @Override
    public void teleport(ServerPlayerEntity player, boolean safeCheck, TeleportCost cost) {
        if (player.getServer() == null) return;
        ServerWorld destinationWorld = player.getServer().getWorld(location.dimension());
        if (destinationWorld == null) {
            player.sendMessage(Text.translatable("§cThat world does not exist! %s", location.dimension().getValue().toString()));
            return;
        }
        if (safeCheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, location.pos())) {
                player.sendMessage(Text.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        cost.applyCost(player,location.pos());
        player.teleport(destinationWorld, location.pos().getX() + 0.5, location.pos().getY(), location.pos().getZ() + 0.5, PositionFlag.DELTA, player.headYaw, player.lastPitch, false);
        player.closeHandledScreen();
    }

    @Override
    public BlockPos getPos(MinecraftServer server) {
        return location.pos();
    }

    @Override
    public ServerPlayerEntity getPlayer(MinecraftServer server) {
        return null;
    }

    @Override
    public Integer getDistance(ServerPlayerEntity player) {
        if (player.getWorld().getRegistryKey().equals(location.dimension())){
            return TeleportAction.distanceBetween(player.getBlockPos(), location.pos());
        }
        return TeleportAction.distanceBetween(player.getBlockPos(), BlockPos.ofFloored(0, 0, 0));
    }
}
