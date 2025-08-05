package infinityi.inventoryMenu.PlaceholdersResolver;

import infinityi.inventoryMenu.ItemAction.Action;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderData {

    public static Map<String, String> ofPlayer(ServerPlayerEntity player) {
        Map<String, String> data = new HashMap<>();
        data.put("xp_level", String.valueOf(player.experienceLevel));
        data.put("playername", player.getName().getString());
        data.put("worlddays", String.valueOf(player.getWorld().getTime() / 24000L + 1));
        return data;
    }

    public static Map<String, String> ofAction(Action action, ServerPlayerEntity player) {
        return action.placeholderData(player);
    }

}

