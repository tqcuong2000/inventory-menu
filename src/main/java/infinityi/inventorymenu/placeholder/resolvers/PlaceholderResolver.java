package infinityi.inventorymenu.placeholder.resolvers;

import infinityi.inventorymenu.placeholder.providers.PlaceholderProvider;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public interface PlaceholderResolver {

    static Component resolve(Component input, Set<PlaceholderProvider> providers, ServerPlayer player) {
        String template = input.getString();
        if (!template.contains("%")) return input;
        String regexString = "%([^%]+)%";
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(template);
        return Component.literal(matcher.replaceAll(match -> {
            String key = match.group(1);
            return providers.stream()
                    .map(p -> p.getKey(key, player))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse("");
        })).setStyle(input.getStyle());
    }

    static List<Component> resolve(List<Component> textList, Set<PlaceholderProvider> providers, ServerPlayer player) {
        return textList.stream().map(line -> resolve(line, providers, player)).collect(Collectors.toList());
    }

}
