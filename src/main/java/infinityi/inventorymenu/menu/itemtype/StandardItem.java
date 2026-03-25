package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import infinityi.inventorymenu.util.DeferredItemStack;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record StandardItem(
    DeferredItemStack item,
    List<Action> actions
) implements MenuItem {
    public static final MapCodec<StandardItem> CODEC =
        RecordCodecBuilder.mapCodec(instance ->
            instance
                .group(
                    DeferredItemStack.CODEC.fieldOf("item").forGetter(
                        StandardItem::item
                    ),
                    Action.LIST_CODEC.optionalFieldOf(
                        "action",
                        Action.EMPTY_LIST
                    ).forGetter(MenuItem::actions)
                )
                .apply(instance, StandardItem::new)
        );

    @Override
    public ItemStack resolveItemStack(ServerPlayer player) {
        try {
            return this.item.resolve();
        } catch (Exception e) {
            InventoryMenu.LOGGER.error(
                "Failed to resolve menu item stack from {}",
                this.item.data(),
                e
            );
            ItemStack errorItem = Items.BARRIER.getDefaultInstance();
            errorItem.set(
                DataComponents.CUSTOM_NAME,
                Component.literal("Invalid menu item")
            );
            return errorItem;
        }
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.ITEM;
    }
}
