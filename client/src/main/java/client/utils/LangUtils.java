package client.utils;

import client.Main;
import client.config.ConfigLoader;
import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;

public class LangUtils {

    private final Map<String, StringProperty> props = new HashMap<>();

    /**
     * Constructor for the {@link LangUtils} singleton
     *
     * @param configLoader Injected reference to the {@link ConfigLoader}
     */
    @Inject
    public LangUtils(ConfigLoader configLoader) {
        Locale.setDefault(Locale.ENGLISH);
        var rb = ResourceBundle.getBundle(Main.BUNDLE_NAME, configLoader.getLocale());

        rb.keySet()
                .forEach(key -> {
                    String shortKey = key.split("\\.")[1];
                    StringProperty newProp = new SimpleStringProperty();
                    props.put(shortKey, newProp);
                    newProp.set(rb.getString(key));
                });
    }

    /**
     * Getter for the internationalized string properties
     *
     * @return The internationalized string properties
     */
    public Map<String, StringProperty> getProps() {
        return props;
    }

    /**
     * Changes the application locale by updating the string properties
     *
     * @param l The new locale of the application
     */
    public void setLocale(Locale l) {
        var rb = ResourceBundle.getBundle(Main.BUNDLE_NAME, l);

        rb.keySet()
                .forEach(key -> {
                    String shortKey = key.split("\\.")[1];
                    props.get(shortKey).set(rb.getString(key));
                });
    }

    /**
     * Returns the name of the bundle label for a specific language
     * @param l The locale corresponding to the language
     * @return The name of the label in the bundle
     */
    public String getLabelFromLocale(Locale l) {
        return switch (l.toLanguageTag()) {
            case "en" -> "english";
            case "pl-PL" -> "polish";
            case "ro-RO" -> "romanian";
            case "nl-NL" -> "dutch";
            default -> "";
        };
    }
}
