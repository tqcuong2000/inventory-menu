package infinityi.inventorymenu.menulayout.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import infinityi.inventorymenu.menulayout.layout.MenuItemType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

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
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        return getItem().map(item -> item.resolveItemStack(player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.DEFINED;
    }

    private Optional<MenuItem> getItem(){
        return InventoryMenu.getDataManager().items().getItem(id);
    }
}
