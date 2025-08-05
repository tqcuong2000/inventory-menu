package infinityi.inventoryMenu.MenuLayout.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record MenuGroup(String name, int index) {
    public static final Codec<MenuGroup> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(MenuGroup::name),
            Codec.INT.optionalFieldOf("index").xmap(i -> i.orElse(0), Optional::ofNullable).forGetter(MenuGroup::index)
    ).apply(inst, MenuGroup::new));

    public static MenuGroup EMPTY = new MenuGroup("", 0);
}
