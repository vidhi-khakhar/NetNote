package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class EmbeddedFile {

    /**
     * Composite primary key for this entity, consisting of
     * the fileId and the noteId
     */
    @EmbeddedId
    private EmbeddedFileId id;

    /**
     * Name of the Embedded File
     */

    private String name;

    /**
     * The file type of the Embedded File
     */

    private String type;

    /**
     * The binary data for the Embedded File, stored as a large
     * object
     */

    @Lob
    private byte[] data;

    /**
     * Many-To-One relationship with the Note entity.
     * The MapsId annotation maps the noteId field in EmbeddedFileId
     */

    @ManyToOne
    @MapsId("noteId")
    private Note note;

    /**
     * Default constructor requested by the JPA
     */

    public EmbeddedFile() {

    }

    /**
     * @param fileId ID of an Embedded File
     * @param noteId ID of a Note
     * @param name Name of the Embedded File
     * @param type The file type of the Embedded File
     * @param data The binary data for the Embedded File, stored as a large object
     * @param note Note linked to the Embedded File
     */

    public EmbeddedFile(Long fileId, Long noteId, String name,
                        String type, byte[] data, Note note) {
        this.id = new EmbeddedFileId(fileId, noteId);
        this.name = name;
        this.type = type;
        this.data = data;
        this.note = note;
    }

    /**
     * Getter for data
     * @return Data of the Embedded File
     */

    public byte[] getData() {
        return data;
    }

    /**
     * Setter for data
     * @param data Data of an Embedded File
     */

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Getter for id
     * @return ID of the associated Embedded File
     */

    public EmbeddedFileId getId() {
        return id;
    }

    /**
     * Getter for name
     * @return Name of the Embedded File
     */

    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name Name of an Embedded File
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for type
     * @return File type of the Embedded File
     */

    public String getType() {
        return type;
    }

    /**
     * Setter for type
     * @param type File type
     */

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for Note
     * @return Note that is linked to the Embedded File
     */

    public Note getNote() {
        return note;
    }

    /**
     * Setter for Note
     * @param note Note entity
     */

    public void setNote(Note note) {
        this.note = note;
    }

    /**
     * @return String representation of an Embedded File
     */

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * @return hashcode of an Embedded File
     */

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     *
     * @param otherFile another File
     * @return whether two Embedded Files are equal
     */

    @Override
    public boolean equals(Object otherFile) {
        return EqualsBuilder.reflectionEquals(this, otherFile);
    }
}
