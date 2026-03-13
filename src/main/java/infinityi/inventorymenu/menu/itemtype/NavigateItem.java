package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.type.MenuNavigationAction;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

public record NavigateItem(MenuNavigationAction navigate, Identifier model, Component custom_name,
                           List<Component> custom_lore) implements MenuItem {
    public static final MapCodec<NavigateItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            MenuNavigationAction.CODEC.forGetter(NavigateItem::navigate),
            Identifier.CODEC.optionalFieldOf("model")
                    .xmap(m -> m.orElse(BuiltInRegistries.ITEM.getKey(Items.ARROW)), Optional::ofNullable)
                    .forGetter(NavigateItem::model),
            ComponentSerialization.CODEC.optionalFieldOf("custom_name")
                    .xmap(t -> t.orElse(Component.empty()), Optional::ofNullable)
                    .forGetter(NavigateItem::custom_name),
            Codec.list(ComponentSerialization.CODEC).optionalFieldOf("custom_lore")
                    .xmap(t -> t.orElse(List.of()), Optional::ofNullable)
                    .forGetter(NavigateItem::custom_lore)
    ).apply(inst, NavigateItem::new));

    @Override
    public List<Action> actions() {
        return List.of(navigate);
    }

    @Override
    public ItemStack resolveItemStack(ServerPlayer player) {
        ItemStack item = Items.ARROW.getDefaultInstance();
        Identifier menuId = navigate.destination();

        Component name = InventoryMenu.dataManager.menus().getMenuName(menuId);
        switch (navigate.navigate()) {
            case "open":
                item = new ItemStack(Items.PAPER);
                item.set(DataComponents.CUSTOM_NAME, Component.translatable("Open %s", name)
                        .setStyle(Style.EMPTY.withItalic(false))
                        .withStyle(ChatFormatting.GREEN));
                break;
            case "next":
                item.set(DataComponents.CUSTOM_NAME, Component.translatable("Next page")
                        .setStyle(Style.EMPTY.withItalic(false))
                        .withStyle(ChatFormatting.GREEN));
                break;
            case "previous":
                item.set(DataComponents.CUSTOM_NAME, Component.translatable("Previous page")
                        .setStyle(Style.EMPTY.withItalic(false))
                        .withStyle(ChatFormatting.GREEN));
                break;
            case "close":
                item = new ItemStack(Items.BARRIER);
                item.set(DataComponents.CUSTOM_NAME, Component.translatable("Close")
                        .setStyle(Style.EMPTY.withItalic(false))
                        .withStyle(ChatFormatting.RED));
                break;
        }
        if (!custom_name.getString().isEmpty()) item.set(DataComponents.CUSTOM_NAME, custom_name);
        if (!custom_lore.isEmpty()) item.set(DataComponents.LORE, new ItemLore(custom_lore));
        if (!model.equals(BuiltInRegistries.ITEM.getKey(Items.ARROW))) item = BuiltInRegistries.ITEM.getValue(model).getDefaultInstance();
        return item;
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.NAVIGATE;
    }

}
