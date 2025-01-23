package client.config;

import client.Main;
import org.junit.jupiter.api.*;

import commons.Collection;

import java.io.*;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTest {

    public static final String filePath = "test-config.json";
    private ConfigLoader loader;
    Collection col1;
    Collection col2;
    Collection col3;

    @BeforeEach
    void setUp() throws UnknownHostException {
        col1 = new Collection("notes-1", "CSEP notes", "http://192.168.1.123/");
        col2 = new Collection("notes-2", "Uni notes", "http://192.168.1.123/");
        col3 = new Collection("notes-3", "Travelling notes", "http://192.168.1.124");
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            w.print("""
                    {
                      "locale" : "nl_NL",
                      "collections" : [
                        {
                          "collectionUniqueName" : "notes-1",
                          "alias" : "CSEP notes",
                          "server" : "http://192.168.1.123/"
                        },
                        {
                          "collectionUniqueName" : "notes-2",
                          "alias" : "Uni notes",
                          "server" : "http://192.168.1.123/"
                        }
                      ],
                      "defaultCollectionUniqueName" : "notes-1"
                    }
                    """);
            w.flush();
        } catch (IOException e) {
            System.err.println("Could not write to the specified file");
        }
        loader = new ConfigLoader(filePath);
    }

    @Test
    void getLocaleTest() {
        assertEquals(Locale.forLanguageTag("nl-NL"), loader.getLocale());
    }

    /**
     * Tests if the data was saved to the {@code test-config.json} file
     * by creating a new {@link ConfigLoader} to parse the contents of the
     * file again.
     */
    @Test
    void setLocaleTest() throws UnknownHostException {
        loader.setLocale(Locale.ENGLISH);
        ConfigLoader newLoader = new ConfigLoader(filePath);
        assertEquals(Locale.ENGLISH, newLoader.getLocale());
    }

    @Test
    void getCollectionsTest() {
        assertEquals(List.of(col1, col2), loader.getCollections());
    }

    @Test
    void setDefaultCollectionUniqueNameTest() throws UnknownHostException {
        loader.setDefaultCollectionUniqueName("notes-2");
        assertEquals("notes-2",loader.getDefaultCollectionUniqueName());
    }

    @Test
    void getDefaultCollectionUniqueName() {
        assertEquals("notes-1",loader.getDefaultCollectionUniqueName());
    }

    @Test
    void addCollectionTest() throws UnknownHostException {
        loader.addCollection(col3);
        ConfigLoader newLoader = new ConfigLoader(filePath);
        assertEquals(List.of(col1, col2, col3), newLoader.getCollections());
    }

    @Test
    void removeCollectionTest() throws UnknownHostException {
        loader.removeCollection("notes-1");
        ConfigLoader newLoader = new ConfigLoader(filePath);
        assertEquals(List.of(col2), newLoader.getCollections());
    }

    @Test
    void defaultValuesTest() throws UnknownHostException {
        new File(filePath).delete();
        ConfigLoader newLoader = new ConfigLoader(filePath);
        assertEquals(Main.DEFAULT_LOCALE, newLoader.getLocale());
        assertEquals(newLoader.getCollections().size(),1);
        assertEquals(newLoader.getCollections().getFirst().getAlias(),"Main");
    }

    @AfterAll
    static void removeTestFile() {
        new File(filePath).delete();
    }
}
