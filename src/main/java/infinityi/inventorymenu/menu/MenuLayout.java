package infinityi.inventorymenu.menu;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.menu.layout.MenuElement;
import infinityi.inventorymenu.util.DeferredItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public record MenuLayout(Component name, Pair<String, Integer> menu_group, Integer rows,
                         Optional<DeferredItemStack> fill, List<MenuElement> items,
                         MenuPredicate predicate) {
    public static final Codec<MenuLayout> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.optionalFieldOf("name", Component.literal("")).forGetter(MenuLayout::name),
            Codec.pair(Codec.STRING.fieldOf("name").codec(), Codec.INT.fieldOf("index").codec()).optionalFieldOf("group", Pair.of("", 0)).forGetter(MenuLayout::menu_group),
            Codec.intRange(1, 6).optionalFieldOf("rows", 3).forGetter(MenuLayout::rows),
            DeferredItemStack.CODEC.optionalFieldOf("fill").forGetter(MenuLayout::fill),
            Codec.list(MenuElement.CODEC).fieldOf("items").forGetter(MenuLayout::items),
            MenuPredicate.CODEC.optionalFieldOf("predicate", MenuPredicate.EMPTY).forGetter(MenuLayout::predicate)
    ).apply(inst, MenuLayout::new));

    public MenuLayout(Component name, Pair<String, Integer> menu_group, Integer rows, List<MenuElement> items,
                      MenuPredicate predicate) {
        this(name, menu_group, rows, Optional.empty(), items, predicate);
    }

    public void open(ServerPlayer player) {
        if (!predicate().test(player, "menu")) return;
        player.openMenu(Menu.createMenu(this, player));
    }
}
