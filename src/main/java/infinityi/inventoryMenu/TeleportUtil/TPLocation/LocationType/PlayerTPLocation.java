package infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocationType;
import infinityi.inventoryMenu.TeleportUtil.TeleportRequestManager.TeleportRequestManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record PlayerTPLocation(String playerName) implements TPLocation {
    public static final MapCodec<PlayerTPLocation> CODEC =
            Codec.STRING.fieldOf("player")
                    .xmap(PlayerTPLocation::new, target -> ((PlayerTPLocation) target).playerName());

    @Override
    public TPLocationType getType() {
        return TPLocationType.PLAYER_LOCATION;
    }

    @Override
    public void teleport(ServerPlayerEntity player, boolean safeCheck) {
        ServerPlayerEntity targetPlayer = getPlayer(player);
        if (targetPlayer == null) {
            player.sendMessage(Text.translatable("%s §cis not online.", this.playerName));
            return;
        }
        TeleportRequestManager.createRequest(player, targetPlayer, safeCheck);
    }


    @Override
    public BlockPos position(ServerPlayerEntity player) {
        ServerPlayerEntity targetPlayer = getPlayer(player);
        if (targetPlayer != null) {
            return targetPlayer.getBlockPos();
        }
        return player.getBlockPos();
    }

    private ServerPlayerEntity getPlayer(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;
        return playerName.length() > 16 ? server.getPlayerManager().getPlayer(UUID.fromString(playerName)) : server.getPlayerManager().getPlayer(playerName);
    }
}
