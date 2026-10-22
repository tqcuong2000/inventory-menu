package infinityi.inventorymenu.action.cost.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.cost.ActionCost;
import infinityi.inventorymenu.action.cost.CostType;
import infinityi.inventorymenu.util.CodecUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.function.Predicate;

public record ItemActionCost(ItemCostData itemData) implements ActionCost {

    public static final MapCodec<ItemActionCost> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemCostData.CODEC.fieldOf("item").forGetter(ItemActionCost::itemData)
    ).apply(inst, ItemActionCost::new));

    @Override
    public CostType<?> getType() {
        return CostType.ITEM;
    }

    @Override
    public boolean check(ServerPlayer player, MinecraftServer server) {
        int amount = itemData.count();
        return amount <= 0 || player.getInventory().clearOrCountMatchingItems(matchesCost(), true, 0, player.inventoryMenu.getCraftSlots()) >= amount;
    }

    @Override
    public void apply(ServerPlayer player, MinecraftServer server) {
        int amount = itemData.count();
        if (amount <= 0) return;
        player.getInventory().clearOrCountMatchingItems(matchesCost(), false, amount, player.inventoryMenu.getCraftSlots());
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.slotsChanged(player.getInventory());
    }

    private Predicate<ItemStack> matchesCost() {
        ItemStack itemStack = itemData.resolveItemStack();
        return stack -> ItemStack.isSameItemSameComponents(stack, itemStack);
    }

    @Override
    public String placeholderText() {
        return itemData.count() + " " + itemData.resolveItemStack().getHoverName().getString();
    }

    public record ItemCostData(Identifier id, int count, Optional<JsonElement> components) {
        public static final Codec<ItemCostData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Identifier.CODEC.fieldOf("id").forGetter(ItemCostData::id),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("count", 1).forGetter(ItemCostData::count),
                CodecUtils.JSON_ELEMENT.optionalFieldOf("components").forGetter(ItemCostData::components)
        ).apply(inst, ItemCostData::new));

        public ItemStack resolveItemStack() {
            if (components.isPresent()) {
                JsonObject json = new JsonObject();
                json.addProperty("id", id.toString());
                json.addProperty("count", 1);
                json.add("components", components.get().deepCopy());
                return ItemStack.CODEC.parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(err -> InventoryMenu.LOGGER.error("Failed to parse ItemStack for item cost {}: {}", id, err))
                        .orElseGet(() -> {
                            Item item = BuiltInRegistries.ITEM.getValue(id);
                            return item != null ? new ItemStack(item, 1) : Items.AIR.getDefaultInstance();
                        });
            }
            Item item = BuiltInRegistries.ITEM.getValue(id);
            return item != null ? new ItemStack(item, 1) : Items.AIR.getDefaultInstance();
        }
    }
}
