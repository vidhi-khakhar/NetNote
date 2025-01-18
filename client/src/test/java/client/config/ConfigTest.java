package client.config;

import org.junit.jupiter.api.*;

import java.util.*;
import commons.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    private Config c1, c2, c3;
    private Collection col1, col2;

    @BeforeEach
    void setUp() {
        col1 = new Collection("my-notes", "My Notes", "http://192.168.1.123");
        col2 = new Collection("my-notes-2", "Uni Notes", "http://192.168.1.124");
        c1 = new Config(Locale.ENGLISH, List.of(col1, col2),"my-notes");
        c2 = new Config(Locale.ENGLISH, List.of(col1, col2),"my-notes");
        c3 = new Config(Locale.forLanguageTag("ro_RO"), List.of(col2),"my-notes-2");
    }

    @Test
    void getLocaleTest() {
        assertEquals(Locale.ENGLISH, c1.getLocale());
    }

    @Test
    void setLocaleTest() {
        c1.setLocale(Locale.forLanguageTag("nl_NL"));
        assertEquals(Locale.forLanguageTag("nl_NL"), c1.getLocale());
    }

    @Test
    void getCollectionsTest() {
        assertEquals(List.of(col1, col2), c1.getCollections());
    }

    @Test
    void setCollectionsTest() {
        c1.setCollections(List.of(col2));
        assertEquals(List.of(col2), c1.getCollections());
    }

    @Test
    void getDefaultCollectionUniqueName() {
        assertEquals("my-notes", c1.getDefaultCollectionUniqueName());
    }

    @Test
    void setDefaultCollectionUniqueName() {
        c1.setDefaultCollectionUniqueName("my-notes2");
        assertEquals("my-notes2", c1.getDefaultCollectionUniqueName());
    }

    @Test
    void equalsSelfTest() {
        assertEquals(c1, c1);
    }

    @Test
    void notEqualsDifferentClassTest() {
        assertNotEquals(c1, new String());
    }

    @Test
    void equalsSameValuesTest() {
        assertEquals(c1, c2);
    }

    @Test
    void notEqualsDifferentValuesTest() {
        assertNotEquals(c1, c3);
    }

    @Test
    void hashCodeEqualsSelfTest() {
        assertEquals(c1.hashCode(), c1.hashCode());
    }

    @Test
    void hashCodeEqualsSameValuesTest() {
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void hashCodeNotEqualsDifferentValuesTest() {
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    @Test
    void toStringTest() {
        String expected = "Config { locale = en, collections = [" +
                col1.toString() +
                ", " +
                col2.toString() +
                "] }";
        assertEquals(expected, c1.toString());
    }
}
