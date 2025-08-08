package infinityi.inventoryMenu.PlaceholdersResolver;

import infinityi.inventoryMenu.ItemAction.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderData {

    public static Map<String, String> ofPlayer(ServerPlayerEntity player) {
        Map<String, String> data = new HashMap<>();
        data.put("xp", String.valueOf(player.experienceLevel));
        data.put("name", player.getName().getString());
        data.put("health", String.format("%.1f", player.getHealth()));
        data.put("maxhealth", String.format("%.1f", player.getMaxHealth()));
        data.put("foodlevel", String.valueOf(player.getHungerManager().getFoodLevel()));
        data.put("saturation", String.format("%.1f", player.getHungerManager().getSaturationLevel()));
        data.put("playerping", String.valueOf(player.networkHandler.getLatency()));

        return data;
    }

    public static Map<String, String> ofWorld(ServerWorld world) {
        Map<String, String> data = new HashMap<>();

        data.put("worldname", world.getRegistryKey().getValue().getPath());
        data.put("worlddays", String.valueOf(world.getTime() / 24000L + 1));
        data.put("difficulty", world.getDifficulty().asString());

        long timeOfDay = world.getTimeOfDay();
        long gameTime = (timeOfDay + 6000) % 24000;
        int hours = (int) (gameTime / 1000);
        int minutes = (int) ((gameTime % 1000) * 60 / 1000.0);
        data.put("worldtime24h", String.format("%02d:%02d", hours, minutes));

        String weather;
        if (world.isThundering()) {
            weather = "Thunder";
        } else if (world.isRaining()) {
            weather = "Rain";
        } else {
            weather = "Clear";
        }
        data.put("weather", weather);
        return data;
    }

    public static Map<String, String> ofServer(MinecraftServer server) {
        Map<String, String> data = new HashMap<>();
        if (server == null) return data;

        data.put("onlineplayers", String.valueOf(server.getCurrentPlayerCount()));
        data.put("maxplayers", String.valueOf(server.getMaxPlayerCount()));
        data.put("motd", server.getServerMotd());
        data.put("version", server.getVersion());

        // Tính toán và định dạng TPS
        double mspt = server.getAverageTickTime();
        double tps = Math.min(20.0, 1000.0 / mspt);
        data.put("server_tps", String.format("%.1f", tps));

        return data;
    }

    public static Map<String, String> ofAction(Action action, ServerPlayerEntity player) {
        return action.placeholderData(player);
    }

}

