package infinityi.inventoryMenu.MenuLayout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.MenuLayout.layout.MenuGroup;
import infinityi.inventoryMenu.MenuLayout.layout.MenuItem;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;
import java.util.Optional;

public record MenuLayout(String id, Text name, MenuGroup group, Integer rows, List<infinityi.inventoryMenu.MenuLayout.layout.MenuItem> items) {
    public static final Codec<MenuLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(MenuLayout::id),
            TextCodecs.CODEC.optionalFieldOf("name", Text.literal("")).forGetter(MenuLayout::name),
            MenuGroup.CODEC.optionalFieldOf("group").xmap(g -> g.orElse(MenuGroup.EMPTY), Optional::ofNullable).forGetter(MenuLayout::group),
            Codec.intRange(1, 6).optionalFieldOf("rows").xmap(i -> i.orElse(3), Optional::ofNullable).forGetter(MenuLayout::rows),
            Codec.list(MenuItem.CODEC).fieldOf("items").forGetter(MenuLayout::items)
    ).apply(instance, MenuLayout::new));
}
