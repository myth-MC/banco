package ovh.mythmc.banco.common.translation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import lombok.RequiredArgsConstructor;
import ovh.mythmc.banco.api.Banco;
import ovh.mythmc.banco.api.logger.LoggerWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RequiredArgsConstructor
public final class BancoLocalization {

    private final List<String> langs = List.of(
        "ca_ES",
        "de_DE",
        "en_US",
        "es_ES",
        "fr_FR",
        "pt_BR",
        "ru_RU",
        "uk_UA",
        "zh_CN"
    );

    private final File baseFile;

    private final LoggerWrapper logger = new LoggerWrapper() {
        @Override
        public void info(final String message, final Object... args) {
            Banco.get().getLogger().info("[i10n] " + message, args);
        }

        @Override
        public void warn(final String message, final Object... args) {
            Banco.get().getLogger().warn("[i10n] " + message, args);
        }

        @Override
        public void error(final String message, final Object... args) {
            Banco.get().getLogger().error("[i10n] " + message, args);
        }
    };

    public void load() {
        final String rootPath = baseFile + File.separator + "lang";
        final Path globalOverridesPath = Paths.get(rootPath, "global_overrides.properties");
        final Path enUsOverridesPath = Paths.get(rootPath, "en_US_overrides.properties");
        //final Path globalOverridesPath = Paths.get(baseFile + File.separator + "lang" + File.separator + "global_overrides.properties");

        if (!Files.exists(globalOverridesPath)) {
            try {
                if (!Files.isDirectory(globalOverridesPath.getParent()))
                    Files.createDirectories(globalOverridesPath.getParent());

                logger.info("Creating override files...", globalOverridesPath.toUri());
                Files.copy(Objects.requireNonNull(Banco.class.getResourceAsStream("/global_overrides.properties")), globalOverridesPath);
                Files.copy(Objects.requireNonNull(Banco.class.getResourceAsStream("/en_US_overrides.properties")), enUsOverridesPath);
                logger.info("Done!");
            } catch (IOException e) {
                logger.error("Error while creating overrides file: {}", e);
            }
        }

        final MiniMessageTranslationStore store = MiniMessageTranslationStore.create(Key.key("banco:i10n"));

        langs.forEach(langTag -> {
            // Get specific locale
            final String baseName = "i10n_" + langTag;
            final Locale locale = Locale.forLanguageTag(langTag.replace("_", "-"));
            final ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale, UTF8ResourceBundleControl.utf8ResourceBundleControl());
            final Path localeOverridesPath = Paths.get(baseFile + File.separator + "lang" + File.separator + langTag + "_overrides.properties");

            // Override global & locale-specific keys
            ResourceBundle overridenBundle = override(globalOverridesPath, resourceBundle);
            if (Files.exists(localeOverridesPath))
                overridenBundle = override(localeOverridesPath, overridenBundle);

            store.registerAll(locale, overridenBundle, true);
        });

        store.defaultLocale(Locale.forLanguageTag(Banco.get().getSettings().get().getDefaultLanguageTag()));
        GlobalTranslator.translator().addSource(store);
    }

    private ResourceBundle override(final Path overrides, final ResourceBundle bundle) {
        Map<String, Object> map = new HashMap<>();

        try (final BufferedReader reader = Files.newBufferedReader(overrides, StandardCharsets.UTF_8)) {
            PropertyResourceBundle overridesBundle = new PropertyResourceBundle(reader);

            for (String k : bundle.keySet()) {
                if (overridesBundle.containsKey(k)) {
                    map.put(k, overridesBundle.getObject(k));
                    Banco.get().getLogger().debug("Overriding language key {} for lang {}", k, bundle.getLocale());
                } else {
                    map.put(k, bundle.getObject(k));
                }
            }
        } catch (final IOException e) {
            logger.error("Error while reading overrides file: {}", e);
            e.printStackTrace(System.err);
        }

        return new MapResourceBundle(map);
    }

}
