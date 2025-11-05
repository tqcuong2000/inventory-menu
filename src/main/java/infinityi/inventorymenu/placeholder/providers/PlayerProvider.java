package infinityi.inventorymenu.placeholder.providers;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PlayerProvider implements PlaceholderProvider {

    public final ServerPlayerEntity player;
    private final Map<String, Supplier<Object>> keySuppliers;

    public PlayerProvider(ServerPlayerEntity player) {
        this.player = player;
        ServerWorld world = player.getEntityWorld();
        this.keySuppliers = Map.of(
                "xp", () -> player.experienceLevel,
                "world_time", () -> getWorldHour(world),
                "name", () -> player.getName().getString(),
                "ping", () -> player.networkHandler.getLatency(),
                "difficulty", () -> world.getDifficulty().asString(),
                "food_level", () -> player.getHungerManager().getFoodLevel(),
                "world_name", () -> world.getRegistryKey().getValue().getPath(),
                "max_health", () -> String.format("%.1f", player.getMaxHealth()),
                "world_days", () -> String.valueOf(world.getTime() / 24000L + 1),
                "saturation", () -> String.format("%.1f", player.getHungerManager().getSaturationLevel())
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> Stat<T> createStat(StatType<T> type, Object value) {
        return type.getOrCreateStat((T) value);
    }

    @Override
    public Optional<String> getKey(String key, ServerPlayerEntity player) {
        if (key.startsWith("stat.")) return Optional.of(String.valueOf(getStatFromString(key)));
        return Optional.ofNullable(keySuppliers.get(key))
                .map(Supplier::get)
                .map(String::valueOf);
    }

    private String getWorldHour(ServerWorld world) {
        long timeOfDay = world.getTimeOfDay();
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
        ServerStatHandler statHandler = player.getStatHandler();
        Optional<StatType<?>> statTypeOpt = Registries.STAT_TYPE.getOptionalValue(Identifier.of("minecraft", category));
        if (statTypeOpt.isEmpty()) {
            return 0;
        }
        StatType<?> statType = statTypeOpt.get();
        Optional<Stat<?>> finalStatOpt;
        String stringId = objectId.contains(":") ? objectId : "minecraft:" + objectId;
        if (statType == Stats.CUSTOM) {
            Identifier customStatId = Identifier.of(stringId);
            finalStatOpt = Registries.CUSTOM_STAT.getOptionalValue(customStatId)
                    .map(Stats.CUSTOM::getOrCreateStat);
        } else {
            Identifier objectIdentifier = Identifier.of(stringId);
            Registry<?> objectRegistry = statType.getRegistry();
            finalStatOpt = Optional.ofNullable(objectRegistry.get(objectIdentifier))
                    .map(statObject -> createStat(statType, statObject));
        }
        return finalStatOpt.map(statHandler::getStat).orElse(0);
    }
}
