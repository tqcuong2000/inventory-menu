package infinityi.inventoryMenu.Commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import infinityi.inventoryMenu.DataParser.MenuDataManager;
import infinityi.inventoryMenu.InventoryMenu;
import infinityi.inventoryMenu.MenuLayout.Menu;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.stream.Collectors;

public class MenuCommand {
    public static void register() {
        System.out.println("REGISTED COMMAND");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("menu")
                    .then(CommandManager.argument("menu_name", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                var loadedMenuNames = MenuDataManager.getLoadedMenuIds().stream()
                                        .map(id -> {
                                            String path = id.getPath();
                                            return path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
                                        })
                                        .collect(Collectors.toList());
                                return CommandSource.suggestMatching(loadedMenuNames, builder);
                            })
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                String menuName = StringArgumentType.getString(context, "menu_name");

                                Identifier menuId = Identifier.of(InventoryMenu.MOD_ID, "menu/" + menuName + ".json");

                                if (player != null) {
                                    MenuDataManager.getMenu(menuId).ifPresentOrElse(
                                            layout -> player.openHandledScreen(Menu.createMenu(layout)),
                                            () -> player.sendMessage(Text.translatable("§cError: Cannot open %s menu because it isn't exist or loaded correctly.", menuName).formatted(Formatting.RED))
                                    );
                                }
                                return 1;
                            }))

            );

            dispatcher.register(CommandManager.literal("warp")
                    .executes(context ->
                    {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player != null) {
                            MenuDataManager.getMenu(Identifier.of(InventoryMenu.MOD_ID, "menu/warp.json")).ifPresentOrElse(layout -> player.openHandledScreen(Menu.createMenu(layout)),
                                    () -> player.sendMessage(Text.translatable("Warp menu is not loaded or does not exist.").formatted(Formatting.RED)));
                        }
                        return 1;
                    }));
        });

    }
}