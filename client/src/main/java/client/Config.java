package client;

import java.util.*;

import commons.Collection;

public class Config {

    private String languageTag;
    private List<Collection> collections;

    /**
     * Parameterless constructor for Jackson
     */
    public Config() {
    }

    /**
     * Constructor for an object that contains application configuration data
     *
     * @param languageTag The language tag for the application locale
     * @param collections The collections the client has access to
     */
    public Config(String languageTag, List<Collection> collections) {
        this.languageTag = languageTag;
        this.collections = collections;
    }

    /**
     * Getter for the application locale language tag
     *
     * @return The application locale language tag
     */
    public String getLanguageTag() {
        return languageTag;
    }

    /**
     * Setter for the application locale language tag
     *
     * @param languageTag The new application locale language tag
     */
    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
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
}
