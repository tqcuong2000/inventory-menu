package infinityi.inventorymenu.itemaction.actions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.itemaction.ActionType;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.List;

public record MessageAction(List<Text> content, boolean isGlobal) implements Action {

    public static final Codec<List<Text>> MESSAGE = Codec.xor(Codec.list(TextCodecs.CODEC), TextCodecs.CODEC)
            .xmap(either -> either.map(list -> list, List::of), list -> {
                if (list.size() == 1) return Either.right(list.getFirst());
                return Either.left(list);
            });
        public static final MapCodec<MessageAction> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                MESSAGE.fieldOf("message").forGetter(MessageAction::content),
                Codec.BOOL.optionalFieldOf("global", false).forGetter(MessageAction::isGlobal)
        ).apply(inst, MessageAction::new));

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {

    }

    @Override
    public ActionType getType() {
        return ActionType.MESSAGE;
    }

}