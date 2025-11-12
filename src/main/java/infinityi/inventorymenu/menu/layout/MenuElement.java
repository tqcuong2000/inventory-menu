package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.util.CodecUtils;
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

    public void onClick(ServerPlayerEntity player){
        if (!meta.condition().test(player, "item")) {
            meta.resolveSound(player, false);
            return;
        }
        meta.resolveSound(player, true);
        if (item.actions().isEmpty()) return;
        for (Action action : item.actions()){
            action.execute(player);
        }
    }
}
