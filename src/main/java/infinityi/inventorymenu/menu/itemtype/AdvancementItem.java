package infinityi.inventorymenu.menu.itemtype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.menu.layout.MenuItem;
import infinityi.inventorymenu.menu.layout.MenuItemType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

public record AdvancementItem(Identifier questId, boolean showDescription, boolean showComplete, List<Action> actions) implements MenuItem {
    public static final MapCodec<AdvancementItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AdvancementItem::questId),
            Codec.BOOL.optionalFieldOf("show_description", true).forGetter(AdvancementItem::showDescription),
            Codec.BOOL.optionalFieldOf("show_complete", true).forGetter(AdvancementItem::showComplete),
            Action.LIST_CODEC.optionalFieldOf("action", Action.EMPTY_LIST).forGetter(MenuItem::actions)
    ).apply(instance, AdvancementItem::new));

    @Override
    public ItemStack resolveItemStack(ServerPlayer player) {
        ItemStack itemStack = new ItemStack(Items.BOOK);
        AdvancementHolder advancementEntry = player.level().getServer().getAdvancements().get(questId);
        if (advancementEntry == null) return itemStack;
        Advancement advancement = advancementEntry.value();
        DisplayInfo display = advancement.display().orElse(null);
        if (display == null) return itemStack;
        Component name = display.getTitle().copy();
        if (name.getStyle().isEmpty()) name = name.copy()
                .setStyle(Style.EMPTY.withItalic(false))
                .withStyle(ChatFormatting.GREEN);
        ItemStack item = display.getIcon();

        List<Component> list = new ArrayList<>();
        if (showDescription){
            Component description = display.getDescription();
            if (description.getStyle().isEmpty()) description = description.copy()
                    .setStyle(Style.EMPTY.withItalic(false))
                    .withStyle(ChatFormatting.GRAY);
            list.add(description.copy());
        }
        if (showComplete){
            boolean isDone = player.getAdvancements().getOrStartProgress(advancementEntry).isDone();
            list.add(isDone ? Component.translatable("§aCompleted!") : Component.translatable("§cNot complete."));
        }
        item.set(DataComponents.CUSTOM_NAME, name);
        item.set(DataComponents.LORE, new ItemLore(list));
        return item;
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.QUEST;
    }
}
