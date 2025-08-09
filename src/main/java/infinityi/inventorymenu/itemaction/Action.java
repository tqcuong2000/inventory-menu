package infinityi.inventorymenu.itemaction;

import com.mojang.serialization.Codec;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

public interface Action {
    Codec<Action> CODEC = StringIdentifiable.createCodec(ActionType::values)
            .dispatch(Action::getType, ActionType::getCodec);

    void execute(ServerPlayerEntity player, MenuLayout layout);

    ActionType getType();

}