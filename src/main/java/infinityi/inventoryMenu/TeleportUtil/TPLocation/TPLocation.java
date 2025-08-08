package infinityi.inventoryMenu.TeleportUtil.TPLocation;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType.BlockTPLocation;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType.GlobalTPLocation;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.LocationType.PlayerTPLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface TPLocation {

    Codec<TPLocation> PLAYER_BLOCK = Codec.xor(PlayerTPLocation.CODEC, GlobalTPLocation.CODEC).xmap(
            either -> either.map(l -> l, r -> r),
            location -> {
                if (location instanceof PlayerTPLocation playerLoc){
                    return Either.left(playerLoc);
                }
                return Either.right((GlobalTPLocation) location);
            }
    );

    Codec<TPLocation> CODEC = Codec.xor(BlockTPLocation.CODEC, PLAYER_BLOCK).xmap(
            either -> either.map( l -> l, r -> r),
            location -> {
                if (location instanceof  BlockTPLocation blockLoc) return Either.left(blockLoc);
                return Either.right(location);
            }
    );

    void teleport(ServerPlayerEntity player, boolean safeCheck);
    BlockPos getPos(MinecraftServer server);
    ServerPlayerEntity getPlayer(MinecraftServer server);
    Integer getDistance(ServerPlayerEntity player);
}