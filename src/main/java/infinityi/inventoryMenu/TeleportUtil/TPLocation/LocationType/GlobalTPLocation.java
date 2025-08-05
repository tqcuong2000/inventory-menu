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
import net.minecraft.util.math.GlobalPos;

public record GlobalTPLocation(GlobalPos location) implements TPLocation {
    public static final MapCodec<GlobalTPLocation> CODEC =
            GlobalPos.CODEC.fieldOf("location")
                    .xmap(GlobalTPLocation::new, target -> ((GlobalTPLocation) target).location());

    @Override
    public TPLocationType getType() {
        return TPLocationType.GLOBAL_LOCATION;
    }

    @Override
    public void teleport(ServerPlayerEntity player, boolean safecheck) {
        if (player.getServer() == null) return;
        ServerWorld destinationWorld = player.getServer().getWorld(location.dimension());
        BlockPos destinationPos = location.pos();
        if (destinationWorld == null) {
            player.sendMessage(Text.translatable("§cThat world is not exist!", location.dimension().getValue().toString()));
            return;
        }

        if (safecheck) {
            if (TeleportAction.isDangerLocation(destinationWorld, destinationPos)) {
                player.sendMessage(Text.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        player.teleport(destinationWorld, location.pos().getX() + 0.5, location.pos().getY(), location.pos().getZ() + 0.5, PositionFlag.DELTA, player.headYaw, player.lastPitch, false);
    }

    @Override
    public BlockPos position(ServerPlayerEntity player) {
        return location.pos();
    }
}

