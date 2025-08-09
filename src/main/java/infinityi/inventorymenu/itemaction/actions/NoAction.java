package infinityi.inventorymenu.itemaction.actions;

import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.itemaction.ActionType;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;

public record NoAction() implements Action {
    public static final MapCodec<NoAction> CODEC = MapCodec.unit(NoAction::new);

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }

}
