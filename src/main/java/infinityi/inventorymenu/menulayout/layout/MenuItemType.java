package infinityi.inventorymenu.menulayout.layout;

import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.menulayout.itemtype.AdvancementItem;
import infinityi.inventorymenu.menulayout.itemtype.DefinedItem;
import infinityi.inventorymenu.menulayout.itemtype.NavigateItem;
import infinityi.inventorymenu.menulayout.itemtype.StaticItem;
import net.minecraft.util.StringIdentifiable;

public enum MenuItemType implements StringIdentifiable {
    ITEM("item", StaticItem.CODEC),
    QUEST("advancement", AdvancementItem.CODEC),
    NAVIGATE("navigate", NavigateItem.CODEC),
    DEFINED("defined", DefinedItem.CODEC);

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