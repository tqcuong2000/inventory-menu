package infinityi.inventorymenu.mixin;

import infinityi.inventorymenu.menulayout.MenuLayout;
import infinityi.inventorymenu.menulayout.layout.CustomMenuInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onClickSlot", at = @At("HEAD"), cancellable = true)
    private void explorers_interceptOnClickSlot(ClickSlotC2SPacket packet, CallbackInfo ci) {
        ScreenHandler currentScreenHandler = this.player.currentScreenHandler;

        if (currentScreenHandler instanceof GenericContainerScreenHandler genericHandler) {
            Inventory inventory = genericHandler.getInventory();
            if (inventory instanceof CustomMenuInventory customMenuInventory) {
                int slotIndex = packet.slot();
                if (slotIndex >= 0 && slotIndex < inventory.size()) {
                    MenuLayout layout = customMenuInventory.getLayout();
                    layout.items().stream()
                            .filter(item -> item.slot() == slotIndex)
                            .findFirst()
                            .ifPresent(menuItem -> menuItem.action().execute(player, layout));

                }

                ci.cancel();
                this.player.currentScreenHandler.syncState();
            }
        }
    }
}
