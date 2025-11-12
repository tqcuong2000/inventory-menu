package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record DefinedItem(Identifier id) implements MenuItem {
    public static final MapCodec<DefinedItem> CODEC = RecordCodecBuilder.mapCodec(inst-> inst.group(
            Identifier.CODEC.fieldOf("item_id").forGetter(DefinedItem::id)
    ).apply(inst ,DefinedItem::new));
    @Override
    public List<Action> actions() {
        return getItem().map(MenuItem::actions).orElse(List.of());
    }

    @Override
    public @NotNull ItemStack resolveItemStack(ServerPlayerEntity player) {
        return getItem().map(item -> item.resolveItemStack(player)).orElse(getErrorItem());
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.DEFINED;
    }

    private Optional<MenuItem> getItem(){
        return InventoryMenu.dataManager.items().getItem(id);
    }

    private ItemStack getErrorItem(){
        ItemStack errorItem = Items.ARROW.getDefaultStack();
        Text name = Text.literal("§cDefined item §7%s §cnot found.".formatted(id.toString()));
        errorItem.set(DataComponentTypes.CUSTOM_NAME, name);
        errorItem.set(DataComponentTypes.ITEM_MODEL, Registries.ITEM.getId(Items.BARRIER));
        return errorItem;
    }
}
