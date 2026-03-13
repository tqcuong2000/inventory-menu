package infinityi.inventorymenu.placeholder.providers;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;

public class PlayerProvider implements PlaceholderProvider {

    public final ServerPlayer player;
    private final Map<String, Supplier<Object>> keySuppliers;

    public PlayerProvider(ServerPlayer player) {
        this.player = player;
        ServerLevel world = player.level();
        this.keySuppliers = Map.of(
                "xp", () -> player.experienceLevel,
                "world_time", () -> getWorldHour(world),
                "name", () -> player.getName().getString(),
                "ping", () -> player.connection.latency(),
                "difficulty", () -> world.getDifficulty().getSerializedName(),
                "food_level", () -> player.getFoodData().getFoodLevel(),
                "world_name", () -> world.dimension().identifier().getPath(),
                "max_health", () -> String.format("%.1f", player.getMaxHealth()),
                "world_days", () -> String.valueOf(world.getGameTime() / 24000L + 1),
                "saturation", () -> String.format("%.1f", player.getFoodData().getSaturationLevel())
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> Stat<T> createStat(StatType<T> type, Object value) {
        return type.get((T) value);
    }

    @Override
    public Optional<String> getKey(String key, ServerPlayer player) {
        if (key.startsWith("stat.")) return Optional.of(String.valueOf(getStatFromString(key)));
        return Optional.ofNullable(keySuppliers.get(key))
                .map(Supplier::get)
                .map(String::valueOf);
    }

    private String getWorldHour(ServerLevel world) {
        long timeOfDay = world.getDayTime();
        long gameTime = (timeOfDay + 6000) % 24000;
        int hours = (int) (gameTime / 1000);
        int minutes = (int) ((gameTime % 1000) * 60 / 1000.0);
        return String.format("%02d:%02d", hours, minutes);
    }

    public int getStatFromString(String statString) {
        if (statString == null || !statString.startsWith("stat.")) {
            return 0;
        }
        String[] parts = statString.split("\\.", 3);
        if (parts.length < 3) {
            return 0;
        }
        String category = parts[1];
        String objectId = parts[2];
        ServerStatsCounter statHandler = player.getStats();
        Optional<StatType<?>> statTypeOpt = BuiltInRegistries.STAT_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", category));
        if (statTypeOpt.isEmpty()) {
            return 0;
        }
        StatType<?> statType = statTypeOpt.get();
        Optional<Stat<?>> finalStatOpt;
        String stringId = objectId.contains(":") ? objectId : "minecraft:" + objectId;
        if (statType == Stats.CUSTOM) {
            Identifier customStatId = Identifier.parse(stringId);
            finalStatOpt = BuiltInRegistries.CUSTOM_STAT.getOptional(customStatId)
                    .map(Stats.CUSTOM::get);
        } else {
            Identifier objectIdentifier = Identifier.parse(stringId);
            Registry<?> objectRegistry = statType.getRegistry();
            finalStatOpt = Optional.ofNullable(objectRegistry.getValue(objectIdentifier))
                    .map(statObject -> createStat(statType, statObject));
        }
        return finalStatOpt.map(statHandler::getValue).orElse(0);
    }
}
