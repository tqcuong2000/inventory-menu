package infinityi.inventorymenu.event;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public final class InteractEntityEvent extends EventManager {

    private InteractEntityEvent() {
    }

    public static InteractionResult interact(
            ServerPlayer player,
            Level world,
            InteractionHand hand,
            Entity entity,
            @Nullable EntityHitResult entityHitResult
    ) {
        if (world.isClientSide()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        var tags = entity.entityTags();
        if (tags.isEmpty()) return InteractionResult.PASS;
        Optional<String> found = tags
                .stream()
                .filter(tag -> tag.startsWith("menu-"))
                .findFirst();
        if (found.isEmpty()) return InteractionResult.PASS;
        String tagValue = found.get().substring(5).toLowerCase(Locale.ROOT);
        Identifier menuId = Identifier.tryParse(tagValue);
        if (menuId == null) return InteractionResult.PASS;
        return openMenu(menuId, player);
    }

    public static void register() {
    }
}
