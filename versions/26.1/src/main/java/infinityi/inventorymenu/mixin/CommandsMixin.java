package infinityi.inventorymenu.mixin;

import infinityi.inventorymenu.command.MenuCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void inventorymenu$registerMenuCommand(Commands.CommandSelection selection, CommandBuildContext buildContext, CallbackInfo ci) {
        MenuCommand.register(((Commands) (Object) this).getDispatcher());
    }
}
