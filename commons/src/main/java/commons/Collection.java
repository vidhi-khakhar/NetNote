package commons;

import jakarta.persistence.*;

import java.util.Objects;

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
    public Collection() { }

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
     * Setter for collectionUniqueName.
     * @param collectionUniqueName new collectionUniqueName
     */
    public void setCollectionUniqueName(String collectionUniqueName) {
        this.collectionUniqueName = collectionUniqueName;
    }

    /**
     * Getter for alias.
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Setter for collectionUniqueName.
     * @param alias new alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Equals method compares only the Collections' collectionUniqueName.
     * @param o another collection
     * @return whether two collections have the same collectionUniqueName
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return Objects.equals(collectionUniqueName, that.collectionUniqueName);
    }

    /**
     * Hashcode uses only collectionUniqueName.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(collectionUniqueName);
    }
}
