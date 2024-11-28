/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import commons.Collection;
import commons.Note;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NoteTest {
    @Test
    void testDefaultConstructor() {
        Note note = new Note();
        assertNull(note.getTitle());
        assertNull(note.getText());
    }

    @Test
    public void testConstructor() {
        Note note = new Note("Note", "This is a note.");
        assertEquals("Note", note.getTitle());
        assertEquals("This is a note.", note.getText());
    }

    @Test
    public void testSetTitle() {
        Note note = new Note();
        note.setTitle("Updated Title");
        assertEquals("Updated Title", note.getTitle());
    }

    @Test
    public void testSetText() {
        Note note = new Note();
        note.setText("Updated text content");
        assertEquals("Updated text content", note.getText());
    }

    @Test
    void testEquals_same() {
        Note note = new Note("Title", "This is a note.");
        assertTrue(note.equals(note));
    }

    @Test
    void testEquals_equalDifferentObjects() {
        Note note1 = new Note("Title", "This is a note.");
        Note note2 = new Note("Title", "This is a note.");
        assertEquals(note1, note2);
    }

    @Test
    void testEquals_notEqualDifferentObjects() {
        Note note1 = new Note("Title1", "This is a note.");
        Note note2 = new Note("Title2", "This is a different note.");
        assertNotEquals(note1, note2);
    }

    @Test
    void testHashCode_same() {
        Note note = new Note("Title", "This is a note.");
        assertEquals(note.hashCode(), note.hashCode());
    }

    @Test
    void testHashCode_equalDifferentObjects() {
        Note note1 = new Note("Title", "This is a note.");
        Note note2 = new Note("Title", "This is a note.");
        assertEquals(note1.hashCode(), note2.hashCode());
    }

    @Test
    void testHashCode_notEqualDifferentObjects() {
        Note note1 = new Note("Title1", "This is a note.");
        Note note2 = new Note("Title2", "This is a different note.");
        assertNotEquals(note1.hashCode(), note2.hashCode());
    }

    @Test
    void testToString() {
        Note note = new Note("Title", "This is a note.");
        assertTrue(note.toString().contains("Title"));
        assertTrue(note.toString().contains("This is a note"));
    }
}


