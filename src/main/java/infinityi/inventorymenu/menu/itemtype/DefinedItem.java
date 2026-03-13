package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record DefinedItem(Identifier id) implements MenuItem {
    public static final MapCodec<DefinedItem> CODEC = RecordCodecBuilder.mapCodec(inst-> inst.group(
            Identifier.CODEC.fieldOf("item_id").forGetter(DefinedItem::id)
    ).apply(inst ,DefinedItem::new));
    @Override
    public List<Action> actions() {
        return getItem().map(MenuItem::actions).orElse(List.of());
    }

    @Override
    public @NotNull ItemStack resolveItemStack(ServerPlayer player) {
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
        ItemStack errorItem = Items.ARROW.getDefaultInstance();
        Component name = Component.literal("§cDefined item §7%s §cnot found.".formatted(id.toString()));
        errorItem.set(DataComponents.CUSTOM_NAME, name);
        errorItem.set(DataComponents.ITEM_MODEL, BuiltInRegistries.ITEM.getKey(Items.BARRIER));
        return errorItem;
    }
}
