package infinityi.inventorymenu.placeholder.providers;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public interface PlaceholderProvider {

    Optional<String> getKey(String key, ServerPlayerEntity player);
}
