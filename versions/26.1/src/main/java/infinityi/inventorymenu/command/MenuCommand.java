package infinityi.inventorymenu.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.dataparser.ConfigManager;
import java.util.ArrayList;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

public class MenuCommand {
    public static void register() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("menu").requires(MenuCommand::hasPermit).then(
                Commands.argument("menu_id", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            var loadedMenuNames = new ArrayList<>(InventoryMenu.dataManager.menus().getIds());
                            var list = loadedMenuNames.stream().map(Identifier::toString).collect(Collectors.toList());
                            if (!ConfigManager.getConfig().menu_command_suggestion) list = new ArrayList<>();
                            return SharedSuggestionProvider.suggest(list, builder);
                        })
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            String menuName = StringArgumentType.getString(context, "menu_id");
                            if (player == null) return 0;
                            Identifier identifier = Identifier.tryParse(menuName);
                            InventoryMenu.dataManager.menus()
                                    .getMenu(identifier)
                                    .ifPresentOrElse(layout ->
                                            layout.open(player), () ->
                                            player.sendSystemMessage(Component.literal("§cThis menu doesn't exist")));
                            return 1;
                        })
        ));
    }

    private static boolean hasPermit(SharedSuggestionProvider source) {
        int level = ConfigManager.getConfig().menu_command_permission;
        var permission = new Permission.HasCommandLevel(PermissionLevel.byId(level));
        return source.permissions().hasPermission(permission);
    }
}
