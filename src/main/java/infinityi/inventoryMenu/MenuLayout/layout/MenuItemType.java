package infinityi.inventoryMenu.MenuLayout.layout;

import com.mojang.serialization.MapCodec;
import infinityi.inventoryMenu.MenuLayout.ItemType.AdvancementItem;
import infinityi.inventoryMenu.MenuLayout.ItemType.NavigateItem;
import infinityi.inventoryMenu.MenuLayout.ItemType.StaticItem;
import net.minecraft.util.StringIdentifiable;

public enum MenuItemType implements StringIdentifiable {
    ITEM("item", StaticItem.CODEC),
    QUEST("quest", AdvancementItem.CODEC),
    NAVIGATE("navigate", NavigateItem.CODEC);

    private final String name;
    private final MapCodec<? extends MenuItem> codec;

    MenuItemType(String name, MapCodec<? extends MenuItem> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public MapCodec<? extends MenuItem> getCodec() {
        return this.codec;
    }
}