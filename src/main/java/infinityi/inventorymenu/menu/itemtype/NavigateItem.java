package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.type.MenuNavigationAction;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public record NavigateItem(MenuNavigationAction navigate, Identifier model, Text custom_name,
                           List<Text> custom_lore) implements MenuItem {
    public static final MapCodec<NavigateItem> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            MenuNavigationAction.CODEC.forGetter(NavigateItem::navigate),
            Identifier.CODEC.optionalFieldOf("model")
                    .xmap(m -> m.orElse(Registries.ITEM.getId(Items.ARROW)), Optional::ofNullable)
                    .forGetter(NavigateItem::model),
            TextCodecs.CODEC.optionalFieldOf("custom_name")
                    .xmap(t -> t.orElse(Text.empty()), Optional::ofNullable)
                    .forGetter(NavigateItem::custom_name),
            Codec.list(TextCodecs.CODEC).optionalFieldOf("custom_lore")
                    .xmap(t -> t.orElse(List.of()), Optional::ofNullable)
                    .forGetter(NavigateItem::custom_lore)
    ).apply(inst, NavigateItem::new));

    @Override
    public List<Action> actions() {
        return List.of(navigate);
    }

    @Override
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        ItemStack item = Items.ARROW.getDefaultStack();
        Identifier menuId = navigate.destination();

        Text name = InventoryMenu.getDataManager().menus().getMenuName(menuId);
        switch (navigate.navigate()) {
            case "open":
                item = new ItemStack(Items.PAPER);
                item.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("Open %s", name)
                        .setStyle(Style.EMPTY.withItalic(false))
                        .formatted(Formatting.GREEN));
                break;
            case "next":
                item.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("Next page")
                        .setStyle(Style.EMPTY.withItalic(false))
                        .formatted(Formatting.GREEN));
                break;
            case "previous":
                item.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("Previous page")
                        .setStyle(Style.EMPTY.withItalic(false))
                        .formatted(Formatting.GREEN));
                break;
            case "close":
                item = new ItemStack(Items.BARRIER);
                item.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("Close")
                        .setStyle(Style.EMPTY.withItalic(false))
                        .formatted(Formatting.RED));
                break;
        }
        if (!custom_name.getString().isEmpty()) item.set(DataComponentTypes.CUSTOM_NAME, custom_name);
        if (!custom_lore.isEmpty()) item.set(DataComponentTypes.LORE, new LoreComponent(custom_lore));
        if (!model.equals(Registries.ITEM.getId(Items.ARROW))) item = Registries.ITEM.get(model).getDefaultStack();
        return item;
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.NAVIGATE;
    }

}
