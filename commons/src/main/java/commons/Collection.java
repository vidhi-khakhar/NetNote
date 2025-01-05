package commons;

import jakarta.persistence.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Entity Collection.
 * Has 3 fields: collectionUniqueName(modifiable), alias, server
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
     * The address of the server that hosts the collection <br>
     * E.g. {@code http://192.168.1.123/}
     */
    private String server;

    /**
     * Default constructor is requested by JPA and Jackson.
     */
    @SuppressWarnings("unused")
    public Collection() {
    }

    /**
     * Recommended constructor for Collection.
     *
     * @param collectionUniqueName Unique name of collection
     * @param alias                User-friendly name
     * @param server               The address of the server where the collection is stored
     */
    public Collection(String collectionUniqueName, String alias, String server) {
        this.collectionUniqueName = collectionUniqueName;
        this.alias = alias;
        this.server = server;
    }

    /**
     * Getter for collectionUniqueName.
     *
     * @return collectionUniqueName
     */
    public String getCollectionUniqueName() {
        return collectionUniqueName;
    }

    /**
     * Setter for collectionUniqueName.
     * @param collectionUniqueName
     */
    public void setCollectionUniqueName(String collectionUniqueName) {
        this.collectionUniqueName = collectionUniqueName;
    }

    /**
     * Getter for alias.
     *
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Setter for alias.
     *
     * @param alias new alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Getter for the address of the server where the collection is located
     *
     * @return The address of the server where the collection is located
     */
    public String getServer() {
        return server;
    }

    /**
     * Setter for the address of the server where the collection is located
     *
     * @param server The new address of the server where the collection is located
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @param anotherCollection another Collection
     * @return whether two Collections are equal
     */
    @Override
    public boolean equals(Object anotherCollection) {
        return EqualsBuilder.reflectionEquals(this, anotherCollection);
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
