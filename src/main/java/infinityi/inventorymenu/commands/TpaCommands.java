package infinityi.inventorymenu.commands;

import com.mojang.brigadier.CommandDispatcher;
import infinityi.inventorymenu.teleportutil.TeleportCost;
import infinityi.inventorymenu.teleportutil.TeleportRequestManager.TeleportRequestManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TpaCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TpaCommands.commands(dispatcher, registryAccess);

        });
    }

    private static void commands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("tpa")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .executes(context -> {
                            ServerPlayerEntity requester = context.getSource().getPlayer();
                            ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");

                            if (requester == null) return 0;

                            if (requester.equals(target)) {
                                requester.sendMessage(Text.translatable("Cannot teleport to yourself").formatted(Formatting.RED));
                                return 0;
                            }

                            TeleportRequestManager.createRequest(requester, target, true, TeleportCost.empty());
                            return 1;
                        }))
        );

        dispatcher.register(CommandManager.literal("tpaccept")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        TeleportRequestManager.acceptRequest(player);
                    }
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("tpadeny")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        TeleportRequestManager.denyRequest(player);
                    }
                    return 1;
                })
        );

    }
}
