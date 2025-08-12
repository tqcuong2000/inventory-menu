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

public record MenuPredicate(Identifier predicate, Action whenTrue, Action whenFalse) {

    public static final MenuPredicate EMPTY = new MenuPredicate(Identifier.of(""), new NoAction(), new NoAction());
    public static final Codec<MenuPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("id").forGetter(MenuPredicate::predicate),
            Action.CODEC.optionalFieldOf("on_true", new NoAction()).forGetter(MenuPredicate::whenTrue),
            Action.CODEC.optionalFieldOf("on_false", new NoAction()).forGetter(MenuPredicate::whenFalse)
    ).apply(inst, MenuPredicate::new));

    public boolean test(ServerPlayerEntity player, MenuLayout layout, String context) {
        ServerWorld serverWorld = player.getWorld();
        if (serverWorld == null) return false;
        MinecraftServer server = serverWorld.getServer();
        LootWorldContext worldContext = new LootWorldContext.Builder(serverWorld)
                .add(LootContextParameters.THIS_ENTITY, player)
                .add(LootContextParameters.ORIGIN, player.getBlockPos().toCenterPos())
                .add(LootContextParameters.DAMAGE_SOURCE, player.getDamageSources().playerAttack(player))
                .build(LootContextTypes.ENTITY);

        LootContext lootContext = new LootContext.Builder(worldContext)
                .random(serverWorld.getSeed())
                .build(Optional.empty());
        boolean result = read(server).map(condition -> condition.test(lootContext)).orElse(true);
        defaultMessage(player, layout, result, context);
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

    private void defaultMessage(ServerPlayerEntity player, MenuLayout layout, boolean result, String context){
        if (!(whenFalse instanceof NoAction) || result) return;
        if (context.equals("menu")) new MessageAction(List
                .of(Text.translatable("§cYou can't open this menu")), false)
                .execute(player, layout);
        if (context.equals("item")) new MessageAction(List
                .of(Text.translatable("§cYou can't perform this action")), false)
                .execute(player, layout);
        player.closeHandledScreen();
    }
}
