package infinityi.inventorymenu.placeholders.providers;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ServerProvider implements PlaceholderProvider {
    private final Map<String, Supplier<Object>> keySuppliers;
    public ServerProvider(MinecraftServer server) {
        this.keySuppliers = Map.of(
                "online_players", server::getCurrentPlayerCount,
                "max_player", server::getMaxPlayerCount,
                "version", server::getVersion,
                "motd", server::getServerMotd,
                "ip", server::getServerIp
        );
    }
    @Override
    public Optional<String> getKey(String key, ServerPlayerEntity player) {
        return Optional.ofNullable(keySuppliers.get(key))
                .map(Supplier::get)
                .map(String::valueOf);
    }
}
