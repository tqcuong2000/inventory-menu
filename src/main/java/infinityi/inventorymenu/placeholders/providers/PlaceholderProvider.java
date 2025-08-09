package infinityi.inventorymenu.placeholders.providers;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public interface PlaceholderProvider {

    Optional<String> getKey(String key, ServerPlayerEntity player);
}
