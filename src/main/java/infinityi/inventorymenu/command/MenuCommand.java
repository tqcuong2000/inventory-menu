package infinityi.inventorymenu.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.dataparser.ConfigManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MenuCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT
                .register((dispatcher, registryAccess, environment) -> {
            //region "/menu" command
            dispatcher
                    .register(CommandManager.literal("menu").requires(
                                    MenuCommand::hasPermit
                            )
                    .then(CommandManager
                            .argument("menu_id", StringArgumentType.greedyString())
                            .suggests((context, builder) -> {
                                var loadedMenuNames = new ArrayList<>(InventoryMenu.dataManager.menus().getIds());
                                var list = loadedMenuNames.stream().map(Identifier::toString).collect(Collectors.toList());
                                if (!ConfigManager.getConfig().menu_command_suggestion) list = new ArrayList<>();
                                return CommandSource.suggestMatching(list, builder);
                            })
                            .executes(context -> {
                                ServerPlayerEntity player = context.getSource().getPlayer();
                                String menuName = StringArgumentType.getString(context, "menu_id");
                                if (player == null) return 0;
                                Identifier identifier = Identifier.tryParse(menuName);
                                InventoryMenu.dataManager.menus()
                                        .getMenu(identifier)
                                        .ifPresentOrElse(layout ->
                                                layout.open(player), () ->
                                                player.sendMessage(Text.literal("§cThis menu doesn't exist")));
                                return 1;
                            }))

            );
            //endregion
        });
    }
    private static boolean hasPermit(CommandSource source) {
        int level = ConfigManager.getConfig().menu_command_permission;
        var permission = new Permission.Level(PermissionLevel.fromLevel(level));
        return source.getPermissions().hasPermission(permission);
    }
}
