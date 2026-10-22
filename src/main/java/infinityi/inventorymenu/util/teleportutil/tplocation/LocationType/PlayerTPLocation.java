package infinityi.inventorymenu.util.teleportutil.tplocation.LocationType;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.type.TeleportAction;
import infinityi.inventorymenu.util.teleportutil.TeleportCost;
import infinityi.inventorymenu.util.teleportutil.TeleportUtils;
import infinityi.inventorymenu.util.teleportutil.tplocation.TPLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record PlayerTPLocation(String playerName) implements TPLocation {

    public static final Codec<PlayerTPLocation> OBJ_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(PlayerTPLocation::playerName)
    ).apply(inst, PlayerTPLocation::new));
    public static final Codec<PlayerTPLocation> CODEC = Codec.xor(Codec.STRING, OBJ_CODEC).xmap(
            either -> either.map(PlayerTPLocation::new, r -> r),
            Either::right
    );

    @Override
    public void teleport(ServerPlayer player, boolean safeCheck, TeleportCost cost) {
        ServerPlayer targetPlayer = getPlayer(player.level().getServer());
        if (targetPlayer == null) {
            player.sendSystemMessage(Component.translatable("§cPlayer is not online."));
            return;
        }
        if (safeCheck) {
            if (TeleportAction.isDangerLocation(targetPlayer.level(), targetPlayer.blockPosition())) {
                player.sendSystemMessage(Component.translatable("§cYou cannot teleport to a dangerous location!"));
                return;
            }
        }
        cost.applyCost(player, targetPlayer.blockPosition());
        player.closeContainer();
        TeleportUtils.teleport(player, targetPlayer.position(), targetPlayer.level());
    }

    @Override
    public BlockPos getPos(MinecraftServer server) {
        if (server == null) return null;
        ServerPlayer player = getPlayer(server);
        if (player == null) return null;
        return player.blockPosition();
    }

    @Override //@Nullable
    public ServerPlayer getPlayer(MinecraftServer server) {
        if (server == null) return null;
        UUID uuid = tryParseUUID(playerName);
        return uuid != null ? server.getPlayerList().getPlayer(uuid) : server.getPlayerList().getPlayerByName(playerName);
    }

    private static UUID tryParseUUID(String input) {
        if (input == null || input.length() != 36) return null;
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Integer getDistance(ServerPlayer player) {
        if (player == null || player.level() == null || player.level().getServer() == null) return 0;
        BlockPos blockPos = getPos(player.level().getServer());
        if (blockPos == null) return 0;
        return TeleportAction.distanceBetween(player.blockPosition(), blockPos);
    }
}
