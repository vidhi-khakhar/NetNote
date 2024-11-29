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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


public class CollectionTest {

    @Test
    void testConstructor() {
        Collection collection = new Collection("uniqueName", "alias");
        assertEquals("uniqueName", collection.getCollectionUniqueName());
        assertEquals("alias", collection.getAlias());
    }

    @Test
    void testSetAlias() {
        Collection collection = new Collection("uniqueName", "alias1");
        collection.setAlias("alias2");
        assertEquals("alias2", collection.getAlias());
    }

    @Test
    void testEquals_same() {
        Collection collection = new Collection("uniqueName", "alias");
        assertTrue(collection.equals(collection));
    }

    @Test
    void testEquals_equalSameValues() {
        Collection collection1 = new Collection("uniqueName", "alias");
        Collection collection2 = new Collection("uniqueName", "alias");
        assertEquals(collection1, collection2);
    }

    @Test
    void testEquals_notEqualDifferentValues() {
        Collection collection1 = new Collection("uniqueName", "alias1");
        Collection collection2 = new Collection("differentName", "alias2");
        assertNotEquals(collection1, collection2);
    }

    @Test
    void testHashCode_same() {
        Collection collection = new Collection("uniqueName", "alias");
        assertEquals(collection.hashCode(), collection.hashCode());
    }

    @Test
    void testHashCode_equalSameValues() {
        Collection collection1 = new Collection("uniqueName", "alias");
        Collection collection2 = new Collection("uniqueName", "alias");
        assertEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    void testHashCode_notEqualDifferentValues() {
        Collection collection1 = new Collection("uniqueName", "alias1");
        Collection collection2 = new Collection("differentName", "alias2");
        assertNotEquals(collection1.hashCode(), collection2.hashCode());
    }

    @Test
    void testToString() {
        Collection collection = new Collection("uniqueName", "alias");
        assertTrue(collection.toString().contains("uniqueName"));
        assertTrue(collection.toString().contains("alias"));
    }

}

