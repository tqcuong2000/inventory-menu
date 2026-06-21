package infinityi.inventorymenu.action.cost;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface ActionCost {
    Codec<ActionCost> ACTION_COST_CODEC = CostType.REGISTRY.byNameCodec()
            .dispatch("type", ActionCost::getType, CostType::codec);

    CostType<?> getType();

    boolean check(ServerPlayer player, MinecraftServer server);
    void apply(ServerPlayer player, MinecraftServer server);
}
