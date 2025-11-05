package infinityi.inventorymenu.menu;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.menu.layout.MenuElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;

public record MenuLayout(Text name, Pair<String, Integer> menu_group, Integer rows, List<MenuElement> items,
                         MenuPredicate predicate) {
    public static final Codec<MenuLayout> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TextCodecs.CODEC.optionalFieldOf("name", Text.literal("")).forGetter(MenuLayout::name),
            Codec.pair(Codec.STRING.fieldOf("name").codec(), Codec.INT.fieldOf("index").codec()).optionalFieldOf("group", Pair.of("", 0)).forGetter(MenuLayout::menu_group),
            Codec.intRange(1, 6).optionalFieldOf("rows", 3).forGetter(MenuLayout::rows),
            Codec.list(MenuElement.CODEC).fieldOf("items").forGetter(MenuLayout::items),
            MenuPredicate.CODEC.optionalFieldOf("predicate", MenuPredicate.EMPTY).forGetter(MenuLayout::predicate)
    ).apply(inst, MenuLayout::new));

    public void open(ServerPlayerEntity player){
        if (!predicate().test(player, "menu")) return;
        player.openHandledScreen(Menu.createMenu(this, player));
    }
}
