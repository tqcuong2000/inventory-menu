package infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType;

import com.mojang.serialization.MapCodec;
import infinityi.inventoryMenu.ItemAction.Actions.TeleportAction;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocationType;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record Vec3dTPLocation(Vec3d position) implements TPLocation {
    public static final MapCodec<Vec3dTPLocation> CODEC =
            Vec3d.CODEC.fieldOf("pos")
                    .xmap(Vec3dTPLocation::new, target -> target.position());

    @Override
    public TPLocationType getType() {
        return TPLocationType.VEC3D_LOCATION;
    }

    @Override
    public void teleport(ServerPlayerEntity player, boolean safecheck) {
        if (player.getServer() == null) return;
        ServerWorld destinationWorld = player.getServer().getWorld(ServerWorld.OVERWORLD);
        if (safecheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, BlockPos.ofFloored(position))) {
                player.sendMessage(Text.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
            player.teleport(destinationWorld, position.getX(), position.getY(), position.getZ(), PositionFlag.DELTA, player.headYaw, player.lastPitch, false);
        }
    }

    @Override
    public BlockPos position(ServerPlayerEntity player) {
        return BlockPos.ofFloored(position);
    }
}
