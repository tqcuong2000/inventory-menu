package infinityi.inventorymenu.action.type;

import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import net.minecraft.server.level.ServerPlayer;

public record NoAction() implements Action {
    public static final MapCodec<NoAction> CODEC = MapCodec.unit(NoAction::new);

    @Override
    public void execute(ServerPlayer player) {
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }

}
