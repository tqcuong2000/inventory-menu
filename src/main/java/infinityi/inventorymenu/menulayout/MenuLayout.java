package infinityi.inventorymenu.menulayout;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;

public record MenuLayout(Text name, Pair<String, Integer> menu_group, Integer rows, List<MenuItem> items) {
    public static final Codec<MenuLayout> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TextCodecs.CODEC.optionalFieldOf("name", Text.literal("")).forGetter(MenuLayout::name),
            Codec.pair(Codec.STRING.fieldOf("name").codec(), Codec.INT.fieldOf("index").codec()).optionalFieldOf("group", Pair.of("", 0)).forGetter(MenuLayout::menu_group),
            Codec.intRange(1, 6).optionalFieldOf("rows", 3).forGetter(MenuLayout::rows),
            Codec.list(MenuItem.CODEC).fieldOf("items").forGetter(MenuLayout::items)
    ).apply(inst, MenuLayout::new));

}
