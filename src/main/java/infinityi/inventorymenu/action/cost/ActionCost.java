package infinityi.inventorymenu.action.cost;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.type.MessageAction;
import infinityi.inventorymenu.util.CodecUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface ActionCost {
    MessageAction EMPTY_FAIL_MESSAGE = new MessageAction(List.of(Component.literal("").withStyle(ChatFormatting.RED)), false);
    Codec<ActionCost> BASE_CODEC = CostType.CODEC
            .dispatch("type", ActionCost::getType, CostType::codec);
    Codec<FailMessageData> FAIL_MESSAGE_DATA_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            MessageAction.CODEC.codec().optionalFieldOf("fail_message", EMPTY_FAIL_MESSAGE).forGetter(FailMessageData::failMessage)
    ).apply(inst, FailMessageData::new));
    Codec<ActionCost> ACTION_COST_CODEC = CodecUtils.mergeFlat(
            BASE_CODEC,
            FAIL_MESSAGE_DATA_CODEC,
            ActionCost::withFailMessage,
            ActionCost::unwrapped,
            ActionCost::failMessageData
    );
    List<ActionCost> EMPTY_LIST = List.of();
    Codec<List<ActionCost>> LIST_CODEC = Codec.xor(ACTION_COST_CODEC, Codec.list(ACTION_COST_CODEC)).xmap(
            either -> either.map(List::of, r -> r),
            list -> {
                if (list.size() == 1) return Either.left(list.getFirst());
                return Either.right(list);
            }
    );

    CostType<?> getType();

    boolean check(ServerPlayer player, MinecraftServer server);

    void apply(ServerPlayer player, MinecraftServer server);

    String placeholderText();

    default MessageAction failMessage() {
        return EMPTY_FAIL_MESSAGE;
    }

    default FailMessageData failMessageData() {
        return new FailMessageData(failMessage());
    }

    default ActionCost unwrapped() {
        return this;
    }

    default boolean hasFailMessage() {
        return failMessage().content().stream().anyMatch(line -> !line.getString().isEmpty());
    }

    default void sendFailMessage(ServerPlayer player) {
        if (hasFailMessage()) failMessage().execute(player);
    }

    private static ActionCost withFailMessage(ActionCost cost, FailMessageData data) {
        return new MessagedActionCost(cost, data.failMessage());
    }

    record FailMessageData(MessageAction failMessage) {
    }

    record MessagedActionCost(ActionCost cost, MessageAction failMessage) implements ActionCost {
        public MessagedActionCost {
            cost = cost.unwrapped();
        }

        @Override
        public CostType<?> getType() {
            return cost.getType();
        }

        @Override
        public boolean check(ServerPlayer player, MinecraftServer server) {
            return cost.check(player, server);
        }

        @Override
        public void apply(ServerPlayer player, MinecraftServer server) {
            cost.apply(player, server);
        }

        @Override
        public String placeholderText() {
            return cost.placeholderText();
        }

        @Override
        public ActionCost unwrapped() {
            return cost;
        }
    }
}
