package infinityi.inventorymenu.action.cost.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.cost.ActionCost;
import infinityi.inventorymenu.action.cost.CostType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public record XPActionCost(int amount, boolean isLevel) implements ActionCost {

    public static final MapCodec<XPActionCost> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("xp").forGetter(XPActionCost::amount),
            Codec.BOOL.optionalFieldOf("is_level", true).forGetter(XPActionCost::isLevel)
    ).apply(inst, XPActionCost::new));

    @Override
    public CostType<?> getType() {
        return CostType.XP;
    }

    @Override
    public boolean check(ServerPlayer player, MinecraftServer server) {
        return (isLevel ? player.experienceLevel : player.totalExperience) >= amount;
    }

    @Override
    public void apply(ServerPlayer player, MinecraftServer server) {
        if (isLevel) {
            player.giveExperienceLevels(-amount);
        } else {
            player.giveExperiencePoints(-amount);
        }
    }

    @Override
    public String placeholderText() {
        return amount + " " + (isLevel ? "levels" : "xp points");
    }
}
