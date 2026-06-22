package infinityi.inventorymenu.action.cost;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.action.cost.type.ItemActionCost;
import infinityi.inventorymenu.action.cost.type.ScoreActionCost;
import infinityi.inventorymenu.action.cost.type.XPActionCost;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public record CostType<T extends ActionCost>(Identifier id, MapCodec<T> codec) {
    private static final Map<Identifier, CostType<?>> BY_ID = new LinkedHashMap<>();

    public static final CostType<XPActionCost> XP = register("xp", XPActionCost.CODEC);
    public static final CostType<ScoreActionCost> SCORE = register("score", ScoreActionCost.CODEC);
    public static final CostType<ItemActionCost> ITEM = register("item", ItemActionCost.CODEC);

    public static final Codec<CostType<?>> CODEC = Identifier.CODEC.comapFlatMap(
            id -> {
                CostType<?> costType = BY_ID.get(id);
                if (costType == null) {
                    return DataResult.error(() -> "Unknown action cost type: " + id);
                }
                return DataResult.success(costType);
            },
            CostType::id
    );

    public static <T extends ActionCost> CostType<T> register(String id, MapCodec<T> codec) {
        Identifier identifier = Identifier.fromNamespaceAndPath("minecraft", id);
        CostType<T> costType = new CostType<>(identifier, codec);
        if (BY_ID.put(identifier, costType) != null) {
            throw new IllegalStateException("Duplicate action cost type: " + identifier);
        }
        return costType;
    }
}
