package infinityi.inventorymenu.menu;

import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.menu.layout.CustomMenuInventory;
import infinityi.inventorymenu.menu.layout.MenuElement;
import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import infinityi.inventorymenu.placeholder.providers.PlaceholderSets;
import infinityi.inventorymenu.placeholder.resolvers.ItemResolver;
import infinityi.inventorymenu.placeholder.resolvers.PlaceholderResolver;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Menu {

    public static MenuProvider createMenu(MenuLayout layout, ServerPlayer player) {
        return new MenuProvider() {
            @Override
            public @NonNull Component getDisplayName() {
                return PlaceholderResolver.resolve(layout.name(), PlaceholderSets.playerServerSet(player), player);
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, @NonNull Inventory playerInventory, @NonNull Player player) {
                Map<Integer, MenuType<?>> sizeMap = new HashMap<>();

                sizeMap.put(1, MenuType.GENERIC_9x1);
                sizeMap.put(2, MenuType.GENERIC_9x2);
                sizeMap.put(3, MenuType.GENERIC_9x3);
                sizeMap.put(4, MenuType.GENERIC_9x4);
                sizeMap.put(5, MenuType.GENERIC_9x5);
                sizeMap.put(6, MenuType.GENERIC_9x6);

                ServerPlayer serverPlayer = (ServerPlayer) player;
                int rows = layout.rows();
                int totalSlots = rows * 9;
                CustomMenuInventory menuInventory = new CustomMenuInventory(layout, totalSlots);

                layout.fill().ifPresent(fillDeferred -> {
                    try {
                        ItemStack fillStack = fillDeferred.resolve();
                        Set<PlaceholderProvider> providers = PlaceholderSets.playerServerSet(serverPlayer);
                        Optional.ofNullable(fillStack.get(DataComponents.CUSTOM_NAME))
                                .ifPresent(name -> fillStack
                                        .set(DataComponents.CUSTOM_NAME, PlaceholderResolver.resolve(name, providers, serverPlayer)));
                        Optional.ofNullable(fillStack.get(DataComponents.LORE))
                                .ifPresent(lore -> fillStack
                                        .set(DataComponents.LORE, new ItemLore(PlaceholderResolver.resolve(lore.lines(), providers, serverPlayer))));
                        for (int i = 0; i < totalSlots; i++) {
                            menuInventory.setItem(i, fillStack.copy());
                        }
                    } catch (Exception e) {
                        InventoryMenu.LOGGER.error("Failed to resolve menu fill item stack", e);
                    }
                });

                for (MenuElement element : layout.items()) {
                    int slot = element.slot();
                    if (slot >= 0 && slot < totalSlots) {
                        ItemResolver resolver = new ItemResolver(element.item(), serverPlayer);
                        menuInventory.setItem(slot, resolver.resolve());
                    }
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
