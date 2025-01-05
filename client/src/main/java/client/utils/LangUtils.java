package client.utils;

import client.Main;
import client.config.ConfigLoader;
import com.google.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;

public class LangUtils {

    private final Map<String, StringProperty> props = new HashMap<>();

    @Inject
    public LangUtils(ConfigLoader configLoader) {
        var rb = ResourceBundle.getBundle(Main.BUNDLE_NAME, configLoader.getLocale());

        rb.keySet()
                .forEach(key -> {
                    String shortKey = key.split("\\.")[1];
                    StringProperty newProp = new SimpleStringProperty();
                    props.put(shortKey, newProp);
                    newProp.set(rb.getString(key));
                });
    }

    public Map<String, StringProperty> getProps() {
        return props;
    }

    public void setLocale(Locale l) {
        var rb = ResourceBundle.getBundle(Main.BUNDLE_NAME, l);

        rb.keySet()
                .forEach(key -> {
                    String shortKey = key.split("\\.")[1];
                    props.get(shortKey).set(rb.getString(key));
                });
    }
}
