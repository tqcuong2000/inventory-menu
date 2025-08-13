package infinityi.inventorymenu.menulayout.itemtype;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import infinityi.inventorymenu.menulayout.layout.MenuItemType;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record AdvancementItem(Identifier questId, boolean showDescription, boolean showComplete) implements MenuItem {
    public static final MapCodec<AdvancementItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(AdvancementItem::questId),
            Codec.BOOL.optionalFieldOf("show_description", true).forGetter(AdvancementItem::showDescription),
            Codec.BOOL.optionalFieldOf("show_complete", true).forGetter(AdvancementItem::showComplete)
    ).apply(instance, AdvancementItem::new));

    @Override
    public List<Action> actions() {
        return Action.EMPTY_LIST;
    }

    @Override
    public ItemStack resolveItemStack(ServerPlayerEntity player) {
        ItemStack itemStack = new ItemStack(Items.BOOK);
        if (player.getServer() == null) return itemStack;
        AdvancementEntry advancementEntry = player.getServer().getAdvancementLoader().get(questId);
        if (advancementEntry == null) return itemStack;
        Advancement advancement = advancementEntry.value();
        AdvancementDisplay display = advancement.display().orElse(null);
        if (display == null) return itemStack;
        Text name = display.getTitle().copy();
        if (name.getStyle().isEmpty()) name = name.copy()
                .setStyle(Style.EMPTY.withItalic(false))
                .formatted(Formatting.GREEN);
        ItemStack item = display.getIcon();

        List<Text> list = new ArrayList<>();
        if (showDescription){
            Text description = display.getDescription();
            if (description.getStyle().isEmpty()) description = description.copy()
                    .setStyle(Style.EMPTY.withItalic(false))
                    .formatted(Formatting.GRAY);
            list.add(description.copy());
        }
        if (showComplete){
            boolean isDone = player.getAdvancementTracker().getProgress(advancementEntry).isDone();
            list.add(isDone ? Text.translatable("§aCompleted!") : Text.translatable("§cNot complete."));
        }
        item.set(DataComponentTypes.CUSTOM_NAME, name);
        item.set(DataComponentTypes.LORE, new LoreComponent(list));
        return item;
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.QUEST;
    }
}
