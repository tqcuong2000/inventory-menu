package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.menu.itemtype.*;
import net.minecraft.util.StringRepresentable;

public enum MenuItemType implements StringRepresentable {
    ITEM("item", StandardItem.CODEC),
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
    public String getSerializedName() {
        return this.name;
    }

    public MapCodec<? extends MenuItem> getCodec() {
        return this.codec;
    }
}