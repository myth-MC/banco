package ovh.mythmc.banco.common.translation;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
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
        Path overrides = Paths.get(baseFile + "/lang/overrides.properties");

        if (!Files.exists(overrides)) {
            try {
                if (!Files.isDirectory(overrides.getParent()))
                    Files.createDirectories(overrides.getParent());

                logger.info("Creating lang/overrides.properties...");
                Files.copy(Objects.requireNonNull(Banco.class.getResourceAsStream("/overrides.properties")),
                        overrides);
                logger.info("Done!");
            } catch (IOException e) {
                logger.error("Error while creating overrides file: {}", e);
            }
        }

        final var translator = TranslationRegistry.create(Key.key("banco", "translation-registry"));

        langs.forEach(langTag -> {
            String baseName = "i10n_" + langTag;
            Locale locale = Locale.forLanguageTag(langTag.replace("_", "-"));
            ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale, UTF8ResourceBundleControl.get());
            translator.registerAll(locale, override(overrides, resourceBundle), true);
        });

        translator.defaultLocale(Locale.forLanguageTag(Banco.get().getSettings().get().getDefaultLanguageTag()));
        GlobalTranslator.translator().addSource(translator);
    }

    private ResourceBundle override(final Path overrides, final ResourceBundle bundle) {
        Map<String, Object> map = new HashMap<>();

        try (final BufferedReader reader = Files.newBufferedReader(overrides, StandardCharsets.UTF_8)) {
            PropertyResourceBundle overridesBundle = new PropertyResourceBundle(reader);

            for (String k : bundle.keySet()) {
                if (overridesBundle.containsKey(k)) {
                    map.put(k, overridesBundle.getObject(k));
                } else {
                    map.put(k, bundle.getObject(k));
                }
            }
        } catch (final IOException e) {
            logger.error("Error while reading overrides file: {}", e);
        }

        return new MapResourceBundle(map);
    }

}
