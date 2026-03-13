package infinityi.inventorymenu.action.type;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.ActionType;
import infinityi.inventorymenu.placeholder.providers.PlaceholderSets;
import infinityi.inventorymenu.placeholder.resolvers.PlaceholderResolver;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerPlayer;

public record MessageAction(List<Component> content, boolean isGlobal) implements Action {

    public static final Codec<List<Component>> MESSAGE = Codec.xor(Codec.list(ComponentSerialization.CODEC), ComponentSerialization.CODEC)
            .xmap(either -> either.map(list -> list, List::of), list -> {
                if (list.size() == 1) return Either.right(list.getFirst());
                return Either.left(list);
            });
    public static final MapCodec<MessageAction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            MESSAGE.fieldOf("message").forGetter(MessageAction::content),
            Codec.BOOL.optionalFieldOf("global", false).forGetter(MessageAction::isGlobal)
    ).apply(inst, MessageAction::new));

    @Override
    public void execute(ServerPlayer player) {
        List<Component> resolvedText = PlaceholderResolver.resolve(content, PlaceholderSets.playerServerSet(player), player);
        for (Component text : resolvedText) {
            if (isGlobal) player.level().getServer().sendSystemMessage(text);
            else player.sendSystemMessage(text);
        }
    }
    @Override
    public ActionType getType() {
        return ActionType.MESSAGE;
    }

}