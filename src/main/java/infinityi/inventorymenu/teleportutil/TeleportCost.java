package infinityi.inventorymenu.teleportutil;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.itemaction.actions.TeleportAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public record TeleportCost(List<Integer> amount, Integer saturation, Boolean isPoint) {

    public static final Codec<List<Integer>> AMOUNT = Codec.xor(Codec.INT, Codec.list(Codec.INT, 2, 2)).xmap(
            either -> either.map(
                    l -> List.of(l, l), r -> r),
            Either::right
    );

    public static final Codec<TeleportCost> OBJ_COST = RecordCodecBuilder.create(inst -> inst.group(
            AMOUNT.fieldOf("amount").forGetter(TeleportCost::amount),
            Codecs.POSITIVE_INT.optionalFieldOf("saturation", 1).forGetter(TeleportCost::saturation),
            Codec.BOOL.optionalFieldOf("is_point", false).forGetter(TeleportCost::isPoint)
    ).apply(inst, TeleportCost::new));

    public static final Codec<TeleportCost> CODEC = Codec.xor(Codec.INT, OBJ_COST).xmap(
            either -> either.map(l -> new TeleportCost(List.of(l, l), 1, false), r -> r),
            Either::right).validate(tpCost -> {
        int min = tpCost.amount.getFirst();
        int max = tpCost.amount.getLast();
        if (min >= 0 && max >= 0 && min <= max) return DataResult.success(tpCost);
        return DataResult.error(() -> "Invalid range for cost");

    });

    public static TeleportCost empty() {
        return new TeleportCost(List.of(0, 0), 1, false);
    }

    public boolean hasCost(ServerPlayerEntity player, BlockPos pos) {
        return player.experienceLevel > calcCost(player, pos);
    }

    public void applyCost(ServerPlayerEntity player, BlockPos pos) {
        int costInLevels = calcCost(player, pos);
        player.addExperienceLevels(-costInLevels);
    }

    public int calcCost(ServerPlayerEntity player, BlockPos pos) {
        if (pos == null) return 0;
        if (Objects.equals(amount.getFirst(), amount.getLast())) return amount.getFirst();
        // Calculate follow formular: min + (max - min) * (distance / (distance + saturation))
        double distance = TeleportAction.distanceBetween(player.getBlockPos(), pos);
        if (distance == 0) return 0;
        return (int) (amount.getFirst() + (amount.getLast() - amount.getFirst()) * (distance / (distance + saturation)));
    }

}