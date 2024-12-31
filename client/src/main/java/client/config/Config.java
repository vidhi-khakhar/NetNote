package client.config;

import java.util.*;

import commons.Collection;

public class Config {

    private Locale locale;
    private List<Collection> collections;

    /**
     * Parameterless constructor for Jackson
     */
    public Config() {
    }

    /**
     * Constructor for an object that contains application configuration data
     *
     * @param locale The application locale
     * @param collections The collections the client has access to
     */
    public Config(Locale locale, List<Collection> collections) {
        this.locale = locale;
        this.collections = collections;
    }

    /**
     * Getter for the application locale
     *
     * @return The application locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Setter for the application locale
     *
     * @param locale The new application locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Getter for the list of collections the client has access to
     *
     * @return The list of collections the client has access to
     */
    public List<Collection> getCollections() {
        return collections;
    }

    /**
     * Setter for the list of collections the client has access to
     *
     * @param collections The new list of collections that
     *                    the client has access to
     */
    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    /**
     * Compares a collection to another object to see if they are equal
     * @param o The object to compare against
     * @return True if the collections are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(locale, config.locale) && Objects.equals(collections, config.collections);
    }

    /**
     * Generates a hashcode for a Config object
     * @return The hashcode used by hash tables
     */
    @Override
    public int hashCode() {
        return Objects.hash(locale, collections);
    }

    /**
     * Generates a string representation of the Config object
     * @return A string representation of the Config object
     */
    @Override
    public String toString() {
        return "Config { locale = " + locale +
                ", collections = " + collections +
                " }";
    }
}
