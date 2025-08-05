package infinityi.inventoryMenu.ItemAction.Actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventoryMenu.ItemAction.Action;
import infinityi.inventoryMenu.ItemAction.ActionType;
import infinityi.inventoryMenu.MenuLayout.MenuLayout;
import infinityi.inventoryMenu.TeleportUtil.TPLocation.TPLocation;
import infinityi.inventoryMenu.TeleportUtil.TeleportCost.ActionCost;
import infinityi.inventoryMenu.TeleportUtil.TeleportCost.FixedCost;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record TeleportAction(TPLocation target, ActionCost cost, Boolean safe_check) implements Action {
    public static final MapCodec<TeleportAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TPLocation.CODEC.fieldOf("target").forGetter(TeleportAction::target),
            ActionCost.CODEC.optionalFieldOf("cost").xmap(c -> c.orElse(new FixedCost(0, false)), Optional::ofNullable).forGetter(TeleportAction::cost),
            Codec.BOOL.optionalFieldOf("safe_check").xmap(b -> b.orElse(false), Optional::ofNullable).forGetter(TeleportAction::safe_check)
    ).apply(instance, TeleportAction::new));

    public static boolean isDangerLocation(ServerWorld world, BlockPos pos) {
        if (world == null || pos == null) {
            return true;
        }
        BlockPos floorPos = pos.down();
        BlockPos headPos = pos.up();
        Chunk chunk = world.getChunk(floorPos);
        if (!world.getBlockState(floorPos).isSolidBlock(world, headPos)) return true;
        BlockState feetState = world.getBlockState(pos);
        BlockState headState = world.getBlockState(headPos);
        if (feetState.shouldSuffocate(world, pos) || headState.shouldSuffocate(world, headPos)) return true;
        return isDangerBlock(feetState) || isDangerBlock(headState);
    }

    private static boolean isDangerBlock(BlockState state) {
        return state.isOf(Blocks.LAVA) || state.isOf(Blocks.FIRE) || state.isOf(Blocks.CACTUS) || state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.POWDER_SNOW);
    }

    @Override
    public void execute(ServerPlayerEntity player, MenuLayout layout) {
        if (cost.hasCost(player, target)) {
            cost.applyCost(player, target);
            target.teleport(player, safe_check);
        } else {
            player.sendMessage(Text.translatable("Not enough experience.").formatted(Formatting.RED));
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.TELEPORT;
    }

    @Override
    public JsonElement getData() {
        return CODEC.codec().encode(this, JsonOps.INSTANCE, new JsonObject())
                .getOrThrow(error -> new IllegalStateException("Error encoding! " + error));
    }

    @Override
    public Map<String, String> placeholderData(ServerPlayerEntity player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("xpcost", String.valueOf(cost.getCost(player, target)));
        placeholders.put("tp_location", String.valueOf(target.position(player)));
        return placeholders;
    }
}
