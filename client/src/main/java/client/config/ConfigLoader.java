package client.config;

import client.Main;
import client.MyModule;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.*;
import java.util.*;

import commons.Collection;

public class ConfigLoader {

    private static final Injector INJECTOR = Guice.createInjector(new MyModule());
    private Config data;
    private String filePath;

    /**
     * Default constructor used by Guice
     */
    @SuppressWarnings("unused")
    public ConfigLoader() {
    }

    /**
     * Constructor for the UserConfigLoader object.
     * Tries to load the pre-existing configurations and creates a new
     * file in the root of the project if it doesn't exist yet.
     *
     * @param filePath The path to the file in which to store the
     *                 configuration data
     */
    public ConfigLoader(String filePath) {
        this.filePath = filePath;
        try {
            File configFile = new File(filePath);
            if (!configFile.exists()) {
                System.out.println("Config file not present. Creating a new config file");
                save();
            }
            data = INJECTOR
                    .getInstance(ObjectMapper.class)
                    .readValue(new File(filePath), Config.class);
        } catch (DatabindException e) {
            System.err.println("Could not bind to the given class.");
        } catch (IOException e) {
            System.err.println("Could not open the user config file.");
        } finally {
            if (data == null) data = new Config(
                    Main.DEFAULT_LOCALE,
                    new ArrayList<>());
        }
    }

    /**
     * Getter for the application locale specified in the configuration.
     *
     * @return The application locale specified in the configuration.
     */
    public Locale getLocale() {
        return data.getLocale();
    }

    /**
     * Setter for the application locale specified in the configuration.
     * Writes the new value to the file.
     *
     * @param l The new locale of the application.
     */
    public void setLocale(Locale l) {
        data.setLocale(l);
        save();
    }

    /**
     * Getter for the list of collections available to the current client
     *
     * @return The list of collections available to the current client
     */
    public List<Collection> getCollections() {
        return data.getCollections();
    }

    /**
     * Add the specified collection to the list of collections
     * available to the current client <br>
     * WARNING: Does not check if the collection is already present
     *
     * @param c The collection to be added to the list of collections
     *          available to the current client
     */
    public void addCollection(Collection c) {
        data.getCollections().add(c);
        save();
    }

    /**
     * Removes a collection from the list of available collections if it matches
     * the specified unique name. Does nothing if no such collection exists.
     *
     * @param uniqueName The unique name (id) of the collection to be removed from
     *                   the list of collections available to the current client.
     */
    public void removeCollection(String uniqueName) {
        data.setCollections(
                data
                        .getCollections()
                        .stream()
                        .filter(c -> !c.getCollectionUniqueName().equals(uniqueName))
                        .toList());
        save();
    }

    /**
     * Writes the current state of the configuration object to the config.json file.
     */
    private void save() {
        try {
            INJECTOR
                    .getInstance(ObjectMapper.class)
                    .writer(new DefaultPrettyPrinter())
                    .writeValue(new File(filePath), data);
        } catch (IOException e) {
            System.err.println("Could not open the user config file.");
        }
    }
}
