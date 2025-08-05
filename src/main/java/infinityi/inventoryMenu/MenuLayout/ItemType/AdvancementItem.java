package infinityi.inventoryMenu.MenuLayout.ItemType;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.Actions.NoAction;
import infinityi.inventoryMenu.MenuLayout.layout.MenuItem;
import infinityi.inventoryMenu.MenuLayout.layout.MenuItemType;
import infinityi.inventoryMenu.MenuLayout.layout.SlotPair;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record AdvancementItem(SlotPair slotPair, Identifier questId) implements MenuItem {
    public static final MapCodec<AdvancementItem> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SlotPair.LIST_CODEC.xmap(l -> new SlotPair(l.getFirst(),l.getLast()), sp -> List.of(sp.row(),sp.column()))
                    .forGetter(AdvancementItem::slotPair),
            Identifier.CODEC.fieldOf("quest_id").forGetter(AdvancementItem::questId)
    ).apply(instance, AdvancementItem::new));

    @Override
    public Integer slot() {
        return slotPair.resolveSlot();
    }

    @Override
    public Action action() {
        return new NoAction();
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
        Text name = display.getTitle();
        Text description = display.getDescription();
        ItemStack item = display.getIcon();
        List<Text> list = new ArrayList<>();
        list.add(description);
        item.set(DataComponentTypes.CUSTOM_NAME, name);
        item.set(DataComponentTypes.LORE, new LoreComponent(list));
        return item;
    }

    @Override
    public MenuItemType getType() {
        return MenuItemType.QUEST;
    }
}
