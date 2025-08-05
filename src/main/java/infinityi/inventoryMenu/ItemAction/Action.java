package infinityi.inventoryMenu.ItemAction;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.StringIdentifiable;

import java.util.Map;

public interface Action {
    Codec<Action> CODEC = StringIdentifiable.createCodec(ActionType::values)
            .dispatch(Action::getType, ActionType::getCodec);

    void execute(ServerPlayerEntity player, MenuLayout layout);

    ActionType getType();

    JsonElement getData();

    Map<String, String> placeholderData(ServerPlayerEntity player);
}