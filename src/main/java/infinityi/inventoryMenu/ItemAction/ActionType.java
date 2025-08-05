package infinityi.inventoryMenu.ItemAction;

import com.mojang.serialization.MapCodec;
import infinityi.inventoryMenu.ItemAction.Actions.MenuNavigationAction;
import infinityi.inventoryMenu.ItemAction.Actions.MessageAction;
import infinityi.inventoryMenu.ItemAction.Actions.NoAction;
import infinityi.inventoryMenu.ItemAction.Actions.TeleportAction;
import net.minecraft.util.StringIdentifiable;

public enum ActionType implements StringIdentifiable {
    MESSAGE("message", MessageAction.CODEC),
    NONE("none", NoAction.CODEC),
    NAVIGATE("navigate", MenuNavigationAction.CODEC),
    TELEPORT("teleport", TeleportAction.CODEC),
    ;
    private final String name;
    private final MapCodec<? extends Action> codec;

    ActionType(String name, MapCodec<? extends Action> codec) {
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public MapCodec<? extends Action> getCodec() {
        return this.codec;
    }
}