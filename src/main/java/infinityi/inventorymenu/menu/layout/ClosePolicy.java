package infinityi.inventorymenu.menu.layout;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum ClosePolicy implements StringRepresentable {
    DEFAULT("default"),
    ON_SUCCESS("on_success"),
    ON_FAILURE("on_failure"),
    ALWAYS("always"),
    NEVER("never");

    public static final Codec<ClosePolicy> CODEC = Codec.xor(
            Codec.BOOL,
            StringRepresentable.fromEnum(ClosePolicy::values)
    ).xmap(
            either -> either.map(
                    bool -> bool ? ClosePolicy.ON_SUCCESS : ClosePolicy.NEVER,
                    policy -> policy
            ),
            policy -> {
                if (policy == ClosePolicy.ON_SUCCESS) return Either.left(true);
                if (policy == ClosePolicy.NEVER) return Either.left(false);
                return Either.right(policy);
            }
    );

    private final String name;

    ClosePolicy(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }
}
