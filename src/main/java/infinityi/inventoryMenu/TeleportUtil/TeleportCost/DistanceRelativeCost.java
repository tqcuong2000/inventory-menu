package infinityi.inventoryMenu.TeleportUtil.TeleportCost;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public record DistanceRelativeCost(Pair<Integer, Integer> range, Integer saturation) implements ActionCost {
    public static final Codec<Pair<Integer, Integer>> MIN_MAX = Codec.pair(Codec.INT, Codec.INT);
    public static final Codec<DistanceRelativeCost> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            MIN_MAX.optionalFieldOf("range").xmap(p -> p.orElse(new Pair<>(0, 30)), Optional::ofNullable).forGetter(DistanceRelativeCost::range),
            Codec.INT.optionalFieldOf("saturation").xmap(i -> i.orElse(300), Optional::ofNullable).forGetter(DistanceRelativeCost::saturation)
    ).apply(inst, DistanceRelativeCost::new));

    @Override
    public boolean hasCost(ServerPlayerEntity player, TPLocation target) {
        return player.experienceLevel < xp_calculate(player, target);
    }

    @Override
    public void applyCost(ServerPlayerEntity player, TPLocation target) {
        player.setExperienceLevel((int) (player.experienceLevel - xp_calculate(player, target)));
    }

    @Override
    public int getCost(ServerPlayerEntity player, TPLocation target) {
        return (int) xp_calculate(player, target);
    }

    private double xp_calculate(ServerPlayerEntity player, TPLocation target) {
        // Calculate follow formular: min + (max - min) * (distance / (distance + saturation))
        double distance = player.getBlockPos().getSquaredDistance(target.position(player));
        return range.getFirst() + (range.getSecond() - range.getFirst()) * (distance / (distance + saturation));
    }
}
