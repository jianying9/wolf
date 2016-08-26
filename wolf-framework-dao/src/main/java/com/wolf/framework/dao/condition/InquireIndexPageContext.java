package com.wolf.framework.dao.condition;

/**
 *
 * @author aladdin
 */
public final class InquireIndexPageContext extends InquirePageContext {

    private final String indexName;
    private final String indexValue;

    public InquireIndexPageContext(String indexName, String indexValue) {
        this.indexName = indexName;
        this.indexValue = indexValue;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getIndexValue() {
        return indexValue;
    }
}
