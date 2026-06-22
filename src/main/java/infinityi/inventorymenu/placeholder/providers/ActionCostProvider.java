package infinityi.inventorymenu.placeholder.providers;

import infinityi.inventorymenu.action.Action;
import infinityi.inventorymenu.action.cost.ActionCost;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionCostProvider implements PlaceholderProvider {
    private final List<ActionCost> costs;

    public ActionCostProvider(List<Action> actions) {
        this.costs = actions.stream()
                .flatMap(action -> action.actionCosts().stream())
                .toList();
    }

    @Override
    public Optional<String> getKey(String key, ServerPlayer player) {
        if (key.equals("action_cost")) {
            return Optional.of(costs.stream()
                    .map(ActionCost::placeholderText)
                    .collect(Collectors.joining(", ")));
        }
        if (key.startsWith("action_cost:")) {
            String bullet = key.substring("action_cost:".length());
            return Optional.of(costs.stream()
                    .map(cost -> bullet + " " + cost.placeholderText())
                    .collect(Collectors.joining("\n")));
        }
        return Optional.empty();
    }
}
