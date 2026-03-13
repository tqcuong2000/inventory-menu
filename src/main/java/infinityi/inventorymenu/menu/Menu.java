package infinityi.inventorymenu.menu;

import infinityi.inventorymenu.menu.layout.CustomMenuInventory;
import infinityi.inventorymenu.menu.layout.MenuElement;
import infinityi.inventorymenu.placeholder.providers.PlaceholderSets;
import infinityi.inventorymenu.placeholder.resolvers.ItemResolver;
import infinityi.inventorymenu.placeholder.resolvers.PlaceholderResolver;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class Menu {

    public static MenuProvider createMenu(MenuLayout layout, ServerPlayer player) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return PlaceholderResolver.resolve(layout.name(),PlaceholderSets.playerServerSet(player), player);
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
                Map<Integer, MenuType<?>> sizeMap = new HashMap<>();

                sizeMap.put(1, MenuType.GENERIC_9x1);
                sizeMap.put(2, MenuType.GENERIC_9x2);
                sizeMap.put(3, MenuType.GENERIC_9x3);
                sizeMap.put(4, MenuType.GENERIC_9x4);
                sizeMap.put(5, MenuType.GENERIC_9x5);
                sizeMap.put(6, MenuType.GENERIC_9x6);

                ServerPlayer serverPlayer = (ServerPlayer) player;
                int rows = layout.rows();
                CustomMenuInventory menuInventory = new CustomMenuInventory(layout, rows * 9);

                for (MenuElement element : layout.items()) {
                    ItemResolver resolver = new ItemResolver(element.item(), serverPlayer);
                    menuInventory.setItem(element.slot(), resolver.resolve());
                }
                MenuType<?> handlerType = sizeMap.get(rows);

                return new ChestMenu(
                        handlerType,
                        syncId,
                        playerInventory,
                        menuInventory,
                        rows
                );
            }
        };
    }

}
