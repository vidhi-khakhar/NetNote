package commons;

import jakarta.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Entity Collection.
 * Has 2 fields: collectionUniqueName(modifiable), alias
 */
@Entity // Marks this class as a JPA entity.
public class Collection {

    /**
     * collectionUniqueName is the unique name of collection used in URL path.
     * It has a setter because it can be renamed.
     * It is the primary key.
     */
    @Id // Specifies the primary key.
    private String collectionUniqueName;

    /**
     * alias is a user-friendly name of collection to be displayed.
     */
    private String alias;

    /**
     * Default constructor is requested by JPA.
     */
    @SuppressWarnings("unused")
    protected Collection() { }

    /**
     * Recommended constructor for Collection.
     * @param collectionUniqueName unique name of collection
     * @param alias user-friendly name
     */
    public Collection(String collectionUniqueName, String alias) {
        this.collectionUniqueName = collectionUniqueName;
        this.alias = alias;
    }

    /**
     * Getter for collectionUniqueName.
     * @return collectionUniqueName
     */
    public String getCollectionUniqueName() {
        return collectionUniqueName;
    }

    /**
     * Getter for alias.
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Setter for alias.
     * @param alias new alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @param anotherCollection another Collection
     * @return whether two Collections are equal
     */
    @Override
    public boolean equals(Object anotherCollection) {
        return EqualsBuilder.reflectionEquals(this,anotherCollection);
    }

    /**
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return String representation
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
