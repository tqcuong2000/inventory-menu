package infinityi.inventoryMenu.ItemAction.Actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.ActionType;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;

public record MessageAction(String content) implements Action {
    public static final MapCodec<MessageAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("value").forGetter(MessageAction::content)
    ).apply(instance, MessageAction::new));

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
        player.sendMessage(Text.literal(this.content));
    }

    @Override
    public ActionType getType() {
        return ActionType.MESSAGE;
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