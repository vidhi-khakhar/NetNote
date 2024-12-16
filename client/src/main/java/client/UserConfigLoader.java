package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class UserConfigLoader {

    private final ObjectMapper om = new ObjectMapper();
    private Map<String, String> data;

    public UserConfigLoader() {
        data = new HashMap<>();
        try {
            TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
            data = om.readValue(new File("config.json"), typeRef);
        } catch (DatabindException e) {
            System.err.println("Could not bind to the given class.");
        } catch (IOException e) {
            System.err.println("Could not open the user config file.");
        }
    }

    public Locale getLocale() {
        String savedLocale = data.get("locale");
        if (savedLocale == null) return Main.DEFAULT_LOCALE;
        if (savedLocale.equals("en")) return Locale.ENGLISH;
        else if (savedLocale.equals("nl-NL")) return Locale.forLanguageTag("nl-NL");
        return Main.DEFAULT_LOCALE;
    }

    public void setLocale(Locale l) {
        data.put("locale", l.toString());
        save();
    }

    private void save() {
        try {
            om.writer(new DefaultPrettyPrinter()).writeValue(new File("config.json"), data);
        } catch (IOException e) {
            System.err.println("Could not open the user config file.");
        }
    }
}
