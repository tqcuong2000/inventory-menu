package infinityi.inventorymenu.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import infinityi.inventorymenu.itemaction.Action;
import infinityi.inventorymenu.itemaction.actions.MessageAction;
import infinityi.inventorymenu.itemaction.actions.NoAction;
import infinityi.inventorymenu.menulayout.MenuLayout;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public record MenuCondition(Identifier predicate, Action whenTrue, Action whenFalse) {

    public static final MenuCondition EMPTY = new MenuCondition(Identifier.of(""), new NoAction(), new NoAction());
    private static final MessageAction DEFAULT_FALSE_ACTION = new MessageAction(
            List.of(Text.translatable("§cYou can't open this menu")), false);
    public static final Codec<MenuCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(MenuCondition::predicate),
            Action.CODEC.optionalFieldOf("on_true", new NoAction()).forGetter(MenuCondition::whenTrue),
            Action.CODEC.optionalFieldOf("on_false", DEFAULT_FALSE_ACTION).forGetter(MenuCondition::whenFalse)
    ).apply(inst, MenuCondition::new));

    public boolean test(ServerPlayerEntity player, MenuLayout layout) {
        ServerWorld serverWorld = player.getWorld();
        if (serverWorld == null) return false;
        MinecraftServer server = serverWorld.getServer();
        LootWorldContext worldContext = new LootWorldContext.Builder(serverWorld)
                .add(LootContextParameters.THIS_ENTITY, player)
                .add(LootContextParameters.ORIGIN, player.getBlockPos().toCenterPos())
                .add(LootContextParameters.DAMAGE_SOURCE, player.getDamageSources().playerAttack(player))
                .build(LootContextTypes.ENTITY);

        LootContext context = new LootContext.Builder(worldContext)
                .random(serverWorld.getSeed())
                .build(Optional.empty());
        boolean result = read(server).map(condition -> condition.test(context)).orElse(true);
        if (!(whenTrue instanceof NoAction) && result) whenTrue.execute(player, layout);
        if (!(whenFalse instanceof NoAction) && !result) whenFalse.execute(player, layout);
        return result;
    }

    public Optional<LootCondition> read(MinecraftServer server) {
        return server.getReloadableRegistries()
                .createRegistryLookup()
                .getOrThrow(RegistryKeys.PREDICATE)
                .getOptional(RegistryKey.of(RegistryKeys.PREDICATE, predicate))
                .map(RegistryEntry::value);
    }
}
