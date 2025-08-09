package infinityi.inventorymenu.itemaction;

import com.mojang.serialization.MapCodec;
import infinityi.inventorymenu.itemaction.actions.MenuNavigationAction;
import infinityi.inventorymenu.itemaction.actions.MessageAction;
import infinityi.inventorymenu.itemaction.actions.NoAction;
import infinityi.inventorymenu.itemaction.actions.TeleportAction;
import net.minecraft.util.StringIdentifiable;

public enum ActionType implements StringIdentifiable {
    NONE("none", NoAction.CODEC),
    MESSAGE("message", MessageAction.CODEC),
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