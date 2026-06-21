package infinityi.inventorymenu.action.cost.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.cost.ActionCost;
import infinityi.inventorymenu.action.cost.CostType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record ItemActionCost(ItemStack itemStack) implements ActionCost {

    public static final MapCodec<ItemActionCost> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStack.CODEC.fieldOf("item").forGetter(ItemActionCost::itemStack)
    ).apply(inst, ItemActionCost::new));

    @Override
    public CostType<?> getType() {
        return CostType.register("item", new CostType<>(CODEC));
    }

    @Override
    public boolean check(ServerPlayer player, MinecraftServer server) {
        return player.getInventory().contains(itemStack);
    }

    @Override
    public void apply(ServerPlayer player, MinecraftServer server) {
        player.getInventory().removeItem(itemStack);
    }
}
