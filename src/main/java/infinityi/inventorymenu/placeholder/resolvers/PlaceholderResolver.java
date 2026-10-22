package infinityi.inventorymenu.placeholder.resolvers;

import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface PlaceholderResolver {

    Pattern PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    static Component resolve(Component input, Set<PlaceholderProvider> providers, ServerPlayer player) {
        String template = input.getString();
        if (!template.contains("%")) return input;
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        return Component.literal(matcher.replaceAll(match -> {
            String key = match.group(1);
            String value = providers.stream()
                    .map(p -> p.getKey(key, player))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse("");
            return Matcher.quoteReplacement(value);
        })).setStyle(input.getStyle());
    }

    static List<Component> resolve(List<Component> textList, Set<PlaceholderProvider> providers, ServerPlayer player) {
        return textList.stream().flatMap(line -> {
            Component resolved = resolve(line, providers, player);
            String text = resolved.getString();
            if (!text.contains("\n")) return List.of(resolved).stream();
            return Arrays.stream(text.split("\n", -1))
                    .map(part -> Component.literal(part).setStyle(resolved.getStyle()));
        }).collect(Collectors.toList());
    }

}
