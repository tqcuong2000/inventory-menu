package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.menu.MenuPredicate;

import java.util.List;

public record MenuItemMeta(SlotPair slot, MenuPredicate condition) {
    public static final Codec<MenuItemMeta> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SlotPair.LIST_CODEC
                    .xmap(l -> new SlotPair(l.getFirst(), l.getLast()), sp -> List.of(sp.row(), sp.column()))
                    .forGetter(MenuItemMeta::slot),
            MenuPredicate.CODEC.optionalFieldOf("predicate", MenuPredicate.EMPTY)
                    .forGetter(MenuItemMeta::condition)
    ).apply(inst, MenuItemMeta::new));
}
