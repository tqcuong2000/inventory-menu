package infinityi.inventorymenu.action.cost.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.cost.ActionCost;
import infinityi.inventorymenu.action.cost.CostType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;

public record ScoreActionCost(String objective, int amount) implements ActionCost {

    public static final MapCodec<ScoreActionCost> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.fieldOf("objective").forGetter(ScoreActionCost::objective),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(ScoreActionCost::amount)
    ).apply(inst, ScoreActionCost::new));

    @Override
    public CostType<?> getType() {
        return CostType.SCORE;
    }

    @Override
    public boolean check(ServerPlayer player, MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();
        Objective obj = scoreboard.getObjective(objective);
        if (obj == null) {
            return false;
        }
        ScoreHolder holder = ScoreHolder.forNameOnly(player.getScoreboardName());
        ReadOnlyScoreInfo scoreInfo = scoreboard.getPlayerScoreInfo(holder, obj);
        if (scoreInfo == null) {
            return false;
        }
        return scoreInfo.value() >= amount;
    }

    @Override
    public void apply(ServerPlayer player, MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();
        Objective obj = scoreboard.getObjective(objective);
        if (obj == null) {
            return;
        }
        ScoreHolder holder = ScoreHolder.forNameOnly(player.getScoreboardName());
        ScoreAccess score = scoreboard.getOrCreatePlayerScore(holder, obj);
        score.set(Math.max(0, score.get() - amount));
    }

    @Override
    public String placeholderText() {
        return amount + " " + objective;
    }
}
