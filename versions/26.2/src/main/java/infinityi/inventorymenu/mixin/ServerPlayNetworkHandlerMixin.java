package infinityi.inventorymenu.mixin;

import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.event.InteractEntityEvent;
import infinityi.inventorymenu.menu.MenuLayout;
import infinityi.inventorymenu.menu.layout.CustomMenuInventory;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(
            method = "handleContainerClick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"
            ),
            cancellable = true
    )
    private void interceptOnClickSlot(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        AbstractContainerMenu currentScreenHandler = this.player.containerMenu;
        if (packet.containerId() != currentScreenHandler.containerId) {
            return;
        }

        if (currentScreenHandler instanceof ChestMenu genericHandler) {
            Container inventory = genericHandler.getContainer();
            if (inventory instanceof CustomMenuInventory customMenuInventory) {
                int slotIndex = packet.slotNum();
                if (slotIndex >= 0 && slotIndex < inventory.getContainerSize()) {
                    this.player.resetLastActionTime();
                    ci.cancel();

                    if (InventoryMenu.dataManager.playerData().canClick(this.player, 100)) {
                        MenuLayout layout = customMenuInventory.getLayout();
                        InventoryMenu.dataManager.playerData().set(player, layout);
                        layout.items().stream()
                                .filter(item -> item.slot() == slotIndex)
                                .findFirst()
                                .ifPresent(element -> element.onClick(player));
                    }
                    this.player.containerMenu.sendAllDataToRemote();
                } else if (packet.containerInput() == ContainerInput.QUICK_MOVE) {
                    this.player.resetLastActionTime();
                    ci.cancel();
                    this.player.containerMenu.sendAllDataToRemote();
                }
            }
        }
    }

    @Inject(method = "handleContainerClose", at = @At("HEAD"))
    private void inventorymenu$onCloseMenu(ServerboundContainerClosePacket packet, CallbackInfo ci) {
        if (InventoryMenu.dataManager != null && InventoryMenu.dataManager.playerData() != null) {
            InventoryMenu.dataManager.playerData().remove(this.player);
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void inventorymenu$onDisconnect(CallbackInfo ci) {
        if (InventoryMenu.dataManager != null && InventoryMenu.dataManager.playerData() != null) {
            InventoryMenu.dataManager.playerData().remove(this.player);
        }
    }

    @Inject(
            method = "handleInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V"
            ),
            cancellable = true
    )
    private void inventorymenu$handleEntityMenu(ServerboundInteractPacket packet, CallbackInfo ci) {
        ServerLevel level = this.player.level();
        Entity entity = level.getEntityOrPart(packet.entityId());
        if (entity == null || !level.getWorldBorder().isWithinBounds(entity.blockPosition())) {
            return;
        }

        if (!this.player.isWithinEntityInteractionRange(entity.getBoundingBox(), 3.0)) {
            return;
        }

        InteractionHand hand = packet.hand();
        if (hand == null) {
            return;
        }

        InteractionResult result = InteractEntityEvent.interact(this.player, level, hand, entity, null);
        if (result == InteractionResult.PASS) {
            return;
        }

        this.player.resetLastActionTime();
        this.player.setShiftKeyDown(packet.usingSecondaryAction());
        ci.cancel();
    }
}
