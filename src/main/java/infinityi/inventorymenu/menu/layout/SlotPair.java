package infinityi.inventorymenu.menu.layout;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record SlotPair(int row, int column) {
    public static final Codec<SlotPair> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.intRange(1, 6).fieldOf("row").forGetter(SlotPair::row),
            Codec.intRange(1, 9).fieldOf("column").forGetter(SlotPair::column)
    ).apply(inst, SlotPair::new));

    public static final MapCodec<List<Integer>> LIST_CODEC = Codec.list(Codec.INT, 2, 2).fieldOf("slot");

    public Integer resolveSlot() {
        return row * 9 - (9 - column) - 1;
    }
}
