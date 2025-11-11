package infinityi.inventorymenu.event;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Optional;

public class InteractEntityEvent extends EventManager{

    public InteractEntityEvent(MinecraftServer server) {
        super(server);
    }

    public ActionResult interact(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        if (world.isClient()) return ActionResult.PASS;
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        var tags = entity.getCommandTags();
        if (tags.isEmpty()) return ActionResult.PASS;
        Optional<String> found = tags.stream()
                .filter(tag -> tag.startsWith("menu-"))
                .findFirst();
        if (found.isEmpty()) return ActionResult.PASS;
        Identifier menuId = Identifier.of(found.get().substring(5).toLowerCase(Locale.ROOT));
        return openMenu(menuId, getServerPlayer(playerEntity));
    }

    public static void register(MinecraftServer server){
        InteractEntityEvent interact = new InteractEntityEvent(server);
        UseEntityCallback.EVENT.register(interact::interact);
    }
}
