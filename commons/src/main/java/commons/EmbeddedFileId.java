package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;


import java.io.Serializable;

@Embeddable
public class EmbeddedFileId implements Serializable {

    /**
     * FileId is part of the composite key
     */

    private Long fileId;

    /**
     * noteId is part of the composite key
     */

    private Long noteId;

    /**
     * Default Constructor requested by the JPA
     */

    public EmbeddedFileId() {}

    /**
     *
     * @param noteId ID of a Note
     * @param fileId ID of an Embedded File
     */

    public EmbeddedFileId(Long noteId,Long fileId) {
        this.noteId = noteId;
        this.fileId = fileId;
    }

    /**
     * Getter for fileId
     * @return ID of an Embedded File
     */

    public Long getFileId() {
        return fileId;
    }

    /**
     * Getter for noteId
     * @return ID of a Note
     */

    public Long getNoteId() {
        return noteId;
    }

    /**
     *
     * @param otherEmbeddedFileId another EmbeddedFileId
     * @return whether two EmbeddedFileId's are equal
     */

    public boolean equals(Object otherEmbeddedFileId) {
        return EqualsBuilder.reflectionEquals(this, otherEmbeddedFileId);
    }

    /**
     * @return Hashcode of an EmbeddedFileId
     */

    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return String representation of an EmbeddedFileId
     */

    public String toString(){
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
