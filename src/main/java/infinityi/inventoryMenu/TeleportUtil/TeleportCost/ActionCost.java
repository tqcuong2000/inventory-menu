package infinityi.inventoryMenu.TeleportUtil.TeleportCost;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import net.minecraft.server.network.ServerPlayerEntity;

public interface ActionCost {
    Codec<ActionCost> OBJECT_CODEC = Codec.either(FixedCost.CODEC.codec(), DistanceRelativeCost.CODEC)
            .xmap(
                    either -> either.map(cost -> cost, cost -> cost),
                    cost -> {
                        if (cost instanceof FixedCost fc) return Either.left(fc);
                        return Either.right((DistanceRelativeCost) cost);
                    }
            );
    Codec<ActionCost> CODEC = Codec.either(Codec.INT, OBJECT_CODEC).xmap(
            either -> either.map(
                    levelCost -> new FixedCost(levelCost, false),
                    actionCost -> actionCost
            ),
            Either::right
    );

    boolean hasCost(ServerPlayerEntity player, TPLocation target);

    void applyCost(ServerPlayerEntity player, TPLocation target);

    int getCost(ServerPlayerEntity player, TPLocation target);
}
