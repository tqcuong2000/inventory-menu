package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.util.CodecUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record MenuElement(MenuItemMeta meta, MenuItem item) {
    public static final Codec<MenuElement> CODEC =
            CodecUtils.mergeFlat(
                    MenuItemMeta.CODEC,
                    MenuItem.CODEC,
                    MenuElement::new,
                    MenuElement::meta,
                    MenuElement::item
            );

    public Integer slot() {
        return meta.slot().resolveSlot();
    }

    public void onClick(ServerPlayer player) {
        AbstractContainerMenu beforeMenu = player.containerMenu;
        if (!meta.condition().test(player, "item")) {
            meta.resolveSound(player, false);
            if ((meta.closePolicy() == ClosePolicy.ON_FAILURE || meta.closePolicy() == ClosePolicy.ALWAYS)
                    && player.containerMenu == beforeMenu) {
                player.closeContainer();
            }
            return;
        }
        meta.resolveSound(player, true);
        if (item.actions().isEmpty()) {
            if ((meta.closePolicy() == ClosePolicy.ON_SUCCESS || meta.closePolicy() == ClosePolicy.ALWAYS)
                    && player.containerMenu == beforeMenu) {
                player.closeContainer();
            }
            return;
        }
        boolean success = Action.executeAll(player, item.actions());
        if (success) {
            if ((meta.closePolicy() == ClosePolicy.ON_SUCCESS || meta.closePolicy() == ClosePolicy.ALWAYS)
                    && player.containerMenu == beforeMenu) {
                player.closeContainer();
            }
        } else {
            if ((meta.closePolicy() == ClosePolicy.ON_FAILURE || meta.closePolicy() == ClosePolicy.ALWAYS)
                    && player.containerMenu == beforeMenu) {
                player.closeContainer();
            }
        }
    }
}
