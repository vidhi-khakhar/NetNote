package client.utils.collection;

public enum CollectionServerStatus {
    SERVER_NOT_REACHABLE("serverNotReachable"),
    COLLECTION_EXISTS("collectionExists"),
    COLLECTION_WILL_BE_CREATED("collectionWillBeCreated");

    private final String bundleLabel;

    CollectionServerStatus(String bundleLabel) {
        this.bundleLabel = bundleLabel;
    }

    /**
     * Getter for the name of the entry in the languages bundle for the given status
     * @return The name of the entry in the languages bundle for the given status
     */
    public String getBundleLabel() {
        return bundleLabel;
    }
}
