package infinityi.inventorymenu.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import infinityi.inventorymenu.action.type.NoAction;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

public interface Action {
    Codec<Action> CODEC = StringRepresentable.fromEnum(ActionType::values)
            .dispatch(Action::getType, ActionType::getCodec);

    Action EMPTY = new NoAction();
    List<Action> EMPTY_LIST = List.of();

    void execute(ServerPlayer player);

    Codec<List<Action>> LIST_CODEC = Codec.xor(CODEC, Codec.list(CODEC)).xmap(
            either -> either.map(List::of, r -> r),
            list -> {
                if (list.size() == 1) return Either.left(list.getFirst());
                return Either.right(list);
            }
    );

    ActionType getType();

}