package infinityi.inventorymenu.menulayout;

import infinityi.inventorymenu.menulayout.layout.CustomMenuInventory;
import infinityi.inventorymenu.menulayout.layout.MenuItem;
import infinityi.inventorymenu.placeholders.resolver.ItemResolver;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class Menu {

    public static NamedScreenHandlerFactory createMenu(MenuLayout layout) {
        return new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {

                return layout.name();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                Map<Integer, ScreenHandlerType<?>> sizeMap = new HashMap<>();

                sizeMap.put(1, ScreenHandlerType.GENERIC_9X1);
                sizeMap.put(2, ScreenHandlerType.GENERIC_9X2);
                sizeMap.put(3, ScreenHandlerType.GENERIC_9X3);
                sizeMap.put(4, ScreenHandlerType.GENERIC_9X4);
                sizeMap.put(5, ScreenHandlerType.GENERIC_9X5);
                sizeMap.put(6, ScreenHandlerType.GENERIC_9X6);

                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                int rows = layout.rows();
                CustomMenuInventory menuInventory = new CustomMenuInventory(layout, rows * 9);

                for (MenuItem menuItem : layout.items()) {
                    ItemResolver resolver = new ItemResolver(menuItem, serverPlayer);
                    menuInventory.setStack(menuItem.slot(), resolver.resolve());
                }

                ScreenHandlerType<?> handlerType = sizeMap.get(rows);

                return new GenericContainerScreenHandler(
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
