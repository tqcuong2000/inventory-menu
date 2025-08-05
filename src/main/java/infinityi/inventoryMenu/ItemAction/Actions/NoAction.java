package infinityi.inventoryMenu.ItemAction.Actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.ActionType;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public record NoAction() implements Action {
    public static final MapCodec<NoAction> CODEC = MapCodec.unit(NoAction::new);

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
    }

    @Override
    public ActionType getType() {
        return ActionType.NONE;
    }

    @Override
    public JsonElement getData() {
        return CODEC.codec().encode(this, JsonOps.INSTANCE, new JsonObject())
                .getOrThrow(error -> new IllegalStateException("Error encoding! " + error));
    }

    @Override
    public Map<String, String> placeholderData(ServerPlayerEntity player) {
        return Map.of();
    }
}
