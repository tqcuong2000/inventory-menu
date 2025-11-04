package infinityi.inventorymenu.util.teleportutil.tplocation.LocationType;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.TeleportUtils;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record PlayerTPLocation(String playerName) implements TPLocation {

    public static final Codec<PlayerTPLocation> OBJ_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(PlayerTPLocation::playerName)
    ).apply(inst, PlayerTPLocation::new));
    public static final Codec<PlayerTPLocation> CODEC = Codec.xor(Codec.STRING,OBJ_CODEC).xmap(
            either -> either.map(PlayerTPLocation::new, r -> r),
            Either::right
    );

    @Override
    public void teleport(ServerPlayerEntity player, boolean safeCheck, TeleportCost cost) {
        ServerPlayerEntity targetPlayer = getPlayer(player.getWorld().getServer());
        if (targetPlayer == null) {
            player.sendMessage(Text.translatable("§cPlayer is not online."));
            return;
        }
        TeleportUtils.teleport(player, targetPlayer.getPos(), targetPlayer.getWorld());
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
        BlockPos blockPos = getPos(player.getWorld().getServer());
        if (blockPos == null) return 0;
        return TeleportAction.distanceBetween(player.getBlockPos(), blockPos);
    }


}
