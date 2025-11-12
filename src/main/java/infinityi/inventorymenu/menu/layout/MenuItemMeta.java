package infinityi.inventorymenu.menu.layout;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.menu.MenuPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record MenuItemMeta(SlotPair slot, MenuPredicate condition, List<String> sounds) {
    public static final Codec<MenuItemMeta> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            SlotPair.LIST_CODEC
                    .xmap(l -> new SlotPair(l.getFirst(), l.getLast()), sp -> List.of(sp.row(), sp.column()))
                    .forGetter(MenuItemMeta::slot),
            MenuPredicate.CODEC.optionalFieldOf("predicate", MenuPredicate.EMPTY)
                    .forGetter(MenuItemMeta::condition),
            Codec.xor(Codec.STRING, Codec.list(Codec.STRING)).xmap(either -> either.map(List::of, r -> r), list -> {
                if (list.size() <= 1) return Either.left(list.getFirst());
                return Either.right(list);
            }).optionalFieldOf("sound", new ArrayList<>()).forGetter(MenuItemMeta::sounds)
    ).apply(inst, MenuItemMeta::new));

    private static final Map<String, Pair<SoundEvent, Float>> soundMap = Map.of(
            "click", createPair(SoundEvents.UI_BUTTON_CLICK.value(),        1.0f),
            "success", createPair(SoundEvents.ENTITY_PLAYER_LEVELUP,        1.5f),
            "fail", createPair(SoundEvents.ENTITY_PLAYER_TELEPORT,          0.4f),
            "villager_success", createPair(SoundEvents.ENTITY_VILLAGER_YES, 1.75f),
            "villager_fail", createPair(SoundEvents.ENTITY_VILLAGER_NO,     1.5f),
            "select", createPair(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),2.0f),
            "open", createPair(SoundEvents.BLOCK_CHEST_OPEN,                1.0f),
            "close", createPair(SoundEvents.BLOCK_CHEST_CLOSE,              1.5f),
            "page_turn", createPair(SoundEvents.ITEM_BOOK_PAGE_TURN,        1.0f),
            "teleport", createPair(SoundEvents.ENTITY_PLAYER_TELEPORT,     1.0f)
    );

    public void resolveSound(ServerPlayerEntity player, boolean success) {
        if (sounds.isEmpty()) return;
        if (success && soundMap.containsKey(sounds.getFirst())){
            var sound = soundMap.get(sounds.getFirst());
            player.playSoundToPlayer(sound.getA(), SoundCategory.UI, 0.5f, sound.getB());
        } else if (!success && soundMap.containsKey(sounds.getLast())){
            var sound = soundMap.get(sounds.getLast());
            player.playSoundToPlayer(sound.getA(), SoundCategory.UI, 0.5f, sound.getB());
        }
    }

    private static Pair<SoundEvent, Float> createPair(SoundEvent soundEvent, float pitch){
        return new Pair<>(soundEvent, pitch);
    }
}
