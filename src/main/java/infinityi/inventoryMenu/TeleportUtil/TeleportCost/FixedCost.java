package infinityi.inventoryMenu.TeleportUtil.TeleportCost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public record FixedCost(int amount, Boolean isPoint) implements ActionCost {
    public static final MapCodec<FixedCost> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("amount").forGetter(FixedCost::amount),
            Codec.BOOL.optionalFieldOf("type").xmap(b -> b.orElse(false), Optional::ofNullable).forGetter(FixedCost::isPoint)
    ).apply(instance, FixedCost::new));

    @Override
    public boolean hasCost(ServerPlayerEntity player, TPLocation target) {
        return player.totalExperience > amount;
    }

    @Override
    public void applyCost(ServerPlayerEntity player, TPLocation target) {
        player.setExperiencePoints(player.totalExperience - amount);
    }

    @Override
    public int getCost(ServerPlayerEntity player, TPLocation target) {
        return amount;
    }
}
