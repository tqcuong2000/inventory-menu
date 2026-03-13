package infinityi.inventorymenu.mixin;

import infinityi.inventorymenu.InventoryMenu;
import infinityi.inventorymenu.menu.MenuLayout;
import infinityi.inventorymenu.menu.layout.CustomMenuInventory;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleContainerClick", at = @At("HEAD"), cancellable = true)
    private void interceptOnClickSlot(ServerboundContainerClickPacket packet, CallbackInfo ci) {
        AbstractContainerMenu currentScreenHandler = this.player.containerMenu;

        if (currentScreenHandler instanceof ChestMenu genericHandler) {
            Container inventory = genericHandler.getContainer();
            if (inventory instanceof CustomMenuInventory customMenuInventory) {
                int slotIndex = packet.slotNum();
                if (slotIndex >= 0 && slotIndex < inventory.getContainerSize()) {
                    MenuLayout layout = customMenuInventory.getLayout();
                    InventoryMenu.dataManager.playerData().set(player, layout);
                    layout.items().stream()
                            .filter(item -> item.slot() == slotIndex)
                            .findFirst()
                            .ifPresent(element -> element.onClick(player));

                }
                ci.cancel();
                this.player.containerMenu.sendAllDataToRemote();
            }
        }
    }
}
