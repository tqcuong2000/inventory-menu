package infinityi.inventorymenu.placeholder.providers;

import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public interface PlaceholderProvider {

    Optional<String> getKey(String key, ServerPlayer player);
}
