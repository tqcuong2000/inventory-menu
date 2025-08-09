package infinityi.inventorymenu.teleportutil.TeleportRequestManager;

import infinityi.inventorymenu.itemaction.actions.TeleportAction;
import infinityi.inventorymenu.teleportutil.TeleportCost;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportRequestManager {
    private static final Map<UUID, TeleportRequest> PENDING_REQUESTS = new ConcurrentHashMap<>();
    private static final int TIMEOUT_SECONDS = 30;

    public static void createRequest(ServerPlayerEntity requester, ServerPlayerEntity target, Boolean safeCheck, TeleportCost cost) {
        if (PENDING_REQUESTS.containsKey(target.getUuid())) {
            requester.sendMessage(Text.translatable("§You already sent a request to %s.", target.getName()));
            return;
        }

        long expiryTime = System.currentTimeMillis() + TIMEOUT_SECONDS * 1000L;
        TeleportRequest request = new TeleportRequest(requester, expiryTime, safeCheck, cost);
        PENDING_REQUESTS.put(target.getUuid(), request);

        requester.sendMessage(Text.translatable("§aSent tpa request to %s.", target.getName().getString()));
        Text acceptText = Text.translatable("§a§l[ACCEPT]")
                .setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/tpaccept"))
                        .withHoverEvent(new HoverEvent.ShowText(Text.translatable("§7Accept teleport request from %s.", requester.getName()))));

        Text denyText = Text.translatable("§c§l[DENY]")
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true)
                        .withClickEvent(new ClickEvent.RunCommand("/tpadeny"))
                        .withHoverEvent(new HoverEvent.ShowText(Text.translatable("Deny teleport request from %s.", requester.getName()))));

        target.sendMessage(Text.translatable("%s§a want to teleport to you", requester.getName()));
        target.sendMessage(acceptText.copy().append(" ").append(denyText));
        target.sendMessage(Text.translatable("Request expired after %s seconds.", Text.literal(String.valueOf(TIMEOUT_SECONDS)).setStyle(Style.EMPTY.withItalic(false))).formatted(Formatting.GRAY));
    }

    public static void acceptRequest(ServerPlayerEntity target) {
        TeleportRequest request = PENDING_REQUESTS.remove(target.getUuid());
        if (request == null) {
            target.sendMessage(Text.translatable("§cYou have no pending teleport request."));
            return;
        }

        if (request.isExpired()) {
            target.sendMessage(Text.translatable("§cTeleport request from %s§c expired.", request.requester().getName()));
            request.requester().sendMessage(Text.translatable("§cTeleport request to %s§c expired", target.getName()));
            return;
        }

        ServerPlayerEntity requester = request.requester();
        if (requester.isDisconnected()) {
            target.sendMessage(Text.translatable("§cPlayer %s §cis no longer online.", requester.getName()));
            return;
        }
        if (request.safeCheck()) {
            if (TeleportAction.isDangerLocation(target.getWorld(), target.getBlockPos())) {
                requester.sendMessage(Text.translatable("§cTeleport request denied: %s §cis in a dangerous location.", target.getName()));
                return;
            }

        }
        if (!request.cost().hasCost(requester,target.getBlockPos())){
            requester.sendMessage(Text.translatable("Not enough experience.").formatted(Formatting.RED));
            return;
        }
        request.cost().applyCost(requester, target.getBlockPos());
        requester.teleport(target.getWorld(), target.capeX, target.capeY, target.capeZ, Collections.emptySet(), target.bodyYaw, target.lastPitch, false);
        requester.sendMessage(Text.translatable("%s§a has accepted your teleport request.", target.getName()));
        target.sendMessage(Text.translatable("§aYou have accepted %s§a's teleport request.", requester.getName()));
    }

    public static void denyRequest(ServerPlayerEntity target) {
        TeleportRequest request = PENDING_REQUESTS.remove(target.getUuid());
        if (request == null) {
            target.sendMessage(Text.translatable("§cYou have no pending teleport request."));
            return;
        }

        request.requester().sendMessage(Text.translatable("%s§c has denied your request", target.getName().getString()));
        target.sendMessage(Text.translatable("§cYou have denied %s§c's teleport request.", request.requester().getName()));
    }
}
