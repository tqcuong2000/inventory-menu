package infinityi.inventorymenu.teleportutil.tplocation.LocationType;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.itemaction.actions.TeleportAction;
import infinityi.inventorymenu.teleportutil.TeleportCost;
import infinityi.inventorymenu.teleportutil.requestmanager.TeleportRequestManager;
import infinityi.inventorymenu.teleportutil.tplocation.TPLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record PlayerTPLocation(String playerName) implements TPLocation {

    public static final Codec<PlayerTPLocation> CODEC = Codec.STRING.xmap(PlayerTPLocation::new, PlayerTPLocation::playerName);

    @Override
    public void teleport(ServerPlayerEntity player, boolean safeCheck, TeleportCost cost) {
        ServerPlayerEntity targetPlayer = getPlayer(player.getServer());
        if (targetPlayer == null) {
            player.sendMessage(Text.translatable("%s §cis not online.", this.playerName));
            return;
        }
        TeleportRequestManager.createRequest(player, targetPlayer, safeCheck, cost);
    }

    @Override
    public BlockPos getPos(MinecraftServer server) {
        ServerPlayerEntity player = getPlayer(server);
        if (player == null) return null;
        return player.getBlockPos();
    }

    @Override //@Nullable
    public ServerPlayerEntity getPlayer(MinecraftServer server) {
        return playerName.length() > 16 ? server.getPlayerManager().getPlayer(UUID.fromString(playerName)) : server.getPlayerManager().getPlayer(playerName);
    }

    @Override
    public Integer getDistance(ServerPlayerEntity player) {
        BlockPos blockPos = getPos(player.getServer());
        if (blockPos == null) return 0;
        return TeleportAction.distanceBetween(player.getBlockPos(), blockPos);
    }


}
