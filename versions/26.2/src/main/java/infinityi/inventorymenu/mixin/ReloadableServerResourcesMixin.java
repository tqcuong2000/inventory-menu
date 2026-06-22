package infinityi.inventorymenu.mixin;

import infinityi.inventorymenu.InventoryMenu;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @Inject(method = "listeners", at = @At("RETURN"), cancellable = true)
    private void inventorymenu$appendReloadListeners(CallbackInfoReturnable<List<PreparableReloadListener>> cir) {
        if (InventoryMenu.dataManager == null) {
            return;
        }

        List<PreparableReloadListener> listeners = new ArrayList<>(cir.getReturnValue());
        for (PreparableReloadListener reloadListener : InventoryMenu.dataManager.reloadListeners()) {
            if (!listeners.contains(reloadListener)) {
                listeners.add(reloadListener);
            }
        }

        cir.setReturnValue(List.copyOf(listeners));
    }
}
