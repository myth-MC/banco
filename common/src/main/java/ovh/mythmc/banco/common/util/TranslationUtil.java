package ovh.mythmc.banco.common.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationUtil {

    private static final String defaultLanguage = "en-US";

    public static void register() {
        load();
    }

    public static void load() {
        final var translator = TranslationRegistry.create(Key.key("banco", "translation-registry"));

        translator.registerAll(Locale.forLanguageTag("en-US"), ResourceBundle.getBundle("i10n_en_US", Locale.forLanguageTag("en-US"), UTF8ResourceBundleControl.get()), true);
        translator.registerAll(Locale.forLanguageTag("es-ES"), ResourceBundle.getBundle("i10n_es_ES", Locale.forLanguageTag("es-ES"), UTF8ResourceBundleControl.get()), true);

        translator.defaultLocale(Locale.forLanguageTag(defaultLanguage));
        GlobalTranslator.translator().addSource(translator);
    }

}
