package infinityi.inventorymenu.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.dataparser.ConfigManager;
import infinityi.inventorymenu.menulayout.Menu;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MenuCommand {
    public static void register() {
        int permissionLevel= ConfigManager.getConfig().menu_command_permission;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            //region "/menu" command
            dispatcher.register(CommandManager.literal("menu").requires(source -> source.hasPermissionLevel(permissionLevel)).then(CommandManager.argument("menu_id", StringArgumentType.greedyString()).suggests((context, builder) -> {
                                var loadedMenuNames = new ArrayList<>(InventoryMenu.getDataManager().menus().getIds());
                                return CommandSource.suggestMatching(loadedMenuNames.stream().map(Identifier::toString).collect(Collectors.toList()), builder);
                            })
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                String menuName = StringArgumentType.getString(context, "menu_id");
                                if (player == null) return 0;
                                Identifier identifier = Identifier.tryParse(menuName);
                                Menu.open(identifier, player);
                                return 1;
                            }))

            );
            //endregion

            //region "/warp" command
//            dispatcher.register(CommandManager.literal("warp").
//                    executes(context -> {
//                        ServerPlayerEntity player = context.getSource().getPlayer();
//                        if (player == null) return 0;
//                        Identifier identifier = Identifier.of("warp");
//                        InventoryMenu.getDataManager().menus().getMenu(identifier).ifPresentOrElse(
//                                layout -> player.openHandledScreen(Menu.createMenu(layout)),
//                                () -> player.sendMessage(Text.translatable("Warp menu is not loaded or does not exist.").formatted(Formatting.RED)));
//                        return 1;
//                    }));
            //endregion
        });
    }
}