package infinityi.inventorymenu.menulayout.layout;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.utils.CodecUtils;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;

public record MenuElement(MenuItemMeta meta, MenuItem item) {
    public static final Codec<MenuElement> CODEC =
            CodecUtils.mergeFlat(
                    MenuItemMeta.CODEC,
                    MenuItem.CODEC,
                    MenuElement::new,
                    MenuElement::meta,
                    MenuElement::item
            );
    public Integer slot(){
        return meta.slot().resolveSlot();
    }

    public void execute(ServerPlayerEntity player, MenuLayout layout){
        if (meta.condition().test(player, layout, "item")) item.action().execute(player, layout);
    }
}
