package client.utils;

import client.config.ConfigLoader;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Locale;

import static client.config.ConfigLoaderTest.filePath;
import static org.junit.jupiter.api.Assertions.*;

public class LangUtilsTest {

    private LangUtils sut;

    @BeforeEach
    public void setUp() throws UnknownHostException {
        try (PrintWriter w = new PrintWriter(new FileWriter(filePath))) {
            w.print("""
                    {
                      "locale" : "nl_NL",
                      "collections" : [ ],
                      "defaultCollectionUniqueName" : "notes-1"
                    }
                    """);
            w.flush();
        } catch (IOException e) {
            System.err.println("Could not write to the specified file");
        }
        sut = new LangUtils(new ConfigLoader(filePath));
    }

    @Test
    public void getLabelFromLocaleEnglishTest() {
        assertEquals("english", sut.getLabelFromLocale(Locale.ENGLISH));
    }

    @Test
    public void getLabelFromLocaleDutchTest() {
        assertEquals("dutch", sut.getLabelFromLocale(Locale.forLanguageTag("nl-NL")));
    }

    @Test
    public void getLabelFromLocaleRomanianTest() {
        assertEquals("romanian", sut.getLabelFromLocale(Locale.forLanguageTag("ro-RO")));
    }

    @Test
    public void getLabelFromLocalePolishTest() {
        assertEquals("polish", sut.getLabelFromLocale(Locale.forLanguageTag("pl-PL")));
    }

    @AfterAll
    static void removeTestFile() {
        new File(filePath).delete();
    }
}
