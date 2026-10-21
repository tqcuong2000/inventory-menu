package infinityi.inventorymenu.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.action.cost.ActionCost;
import infinityi.inventorymenu.action.type.NoAction;
import infinityi.inventorymenu.util.CodecUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

import java.util.ArrayList;
import java.util.List;

public interface Action {
    Codec<Action> BASE_CODEC = StringRepresentable.fromEnum(ActionType::values)
            .dispatch(Action::getType, ActionType::getCodec);
    Codec<ActionCostData> COST_DATA_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ActionCost.LIST_CODEC.optionalFieldOf("action_cost", ActionCost.EMPTY_LIST).forGetter(ActionCostData::actionCosts)
    ).apply(inst, ActionCostData::new));
    Codec<Action> CODEC = CodecUtils.mergeFlat(
            BASE_CODEC,
            COST_DATA_CODEC,
            Action::withCost,
            Action::unwrapped,
            Action::costData
    );

    Action EMPTY = new NoAction();
    List<Action> EMPTY_LIST = List.of();
    Codec<List<Action>> LIST_CODEC = Codec.xor(CODEC, Codec.list(CODEC)).xmap(
            either -> either.map(List::of, r -> r),
            list -> {
                if (list.size() == 1) return Either.left(list.getFirst());
                return Either.right(list);
            }
    );

    void execute(ServerPlayer player);

    ActionType getType();

    default List<ActionCost> actionCosts() {
        return ActionCost.EMPTY_LIST;
    }

    default ActionCostData costData() {
        return new ActionCostData(actionCosts());
    }

    default Action unwrapped() {
        return this;
    }

    default boolean checkCost(ServerPlayer player, MinecraftServer server) {
        for (ActionCost cost : actionCosts()) {
            if (!cost.check(player, server)) return false;
        }
        return true;
    }

    default void applyCost(ServerPlayer player, MinecraftServer server) {
        for (ActionCost cost : actionCosts()) cost.apply(player, server);
    }

    default void sendFailMessages(ServerPlayer player) {
        for (ActionCost cost : actionCosts()) cost.sendFailMessage(player);
    }

    default void executeWithCost(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        if (!checkCost(player, server)) {
            sendFailMessages(player);
            return;
        }
        applyCost(player, server);
        unwrapped().execute(player);
    }

    static boolean executeAll(ServerPlayer player, List<Action> actions) {
        MinecraftServer server = player.level().getServer();
        if (server == null) return false;
        List<ActionCost> costs = new ArrayList<>();
        for (Action action : actions) {
            costs.addAll(action.actionCosts());
        }
        boolean canPay = true;
        for (ActionCost cost : costs) {
            if (!cost.check(player, server)) canPay = false;
        }
        if (!canPay) {
            for (ActionCost cost : costs) cost.sendFailMessage(player);
            return false;
        }
        for (Action action : actions) {
            action.applyCost(player, server);
            action.unwrapped().execute(player);
        }
        return true;
    }

    private static Action withCost(Action action, ActionCostData data) {
        return data.actionCosts().isEmpty() ? action : new CostedAction(action, data.actionCosts());
    }

    record ActionCostData(List<ActionCost> actionCosts) {
    }

    record CostedAction(Action action, List<ActionCost> costs) implements Action {
        public CostedAction {
            action = action.unwrapped();
            costs = List.copyOf(costs);
        }

        @Override
        public void execute(ServerPlayer player) {
            executeWithCost(player);
        }

        @Override
        public ActionType getType() {
            return action.getType();
        }

        @Override
        public List<ActionCost> actionCosts() {
            return costs;
        }

        @Override
        public Action unwrapped() {
            return action;
        }
    }

}
