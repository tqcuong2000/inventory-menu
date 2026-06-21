package infinityi.inventorymenu.action.cost;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record CostType<T extends ActionCost>(MapCodec<T> codec) {
    public static final Registry<CostType<?>> REGISTRY = new MappedRegistry<>(
            ResourceKey.createRegistryKey(Identifier.fromNamespaceAndPath("minecraft", "cost_type")), Lifecycle.stable()
    );

    public static <T extends ActionCost> CostType<T> register(String id, CostType<T> costType) {
        return Registry.register(CostType.REGISTRY, Identifier.fromNamespaceAndPath("minecraft", id), costType);
    }
}
