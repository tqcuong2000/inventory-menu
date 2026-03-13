package infinityi.inventorymenu.placeholder.providers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ServerProvider implements PlaceholderProvider {
    private final Map<String, Supplier<Object>> keySuppliers;

    public ServerProvider(MinecraftServer server) {
        this.keySuppliers = Map.of(
                "online_players", server::getPlayerCount,
                "max_player", server::getMaxPlayers,
                "version", server::getServerVersion,
                "motd", server::getMotd,
                "ip", server::getLocalIp
        );
    }

    @Override
    public Optional<String> getKey(String key, ServerPlayer player) {
        return Optional.ofNullable(keySuppliers.get(key))
                .map(Supplier::get)
                .map(String::valueOf);
    }
}
