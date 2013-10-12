package com.wolf.framework.dao.condition;

/**
 *
 * @author aladdin
 */
public final class InquireRedisIndexContext extends InquirePageContext {

    private final String indexName;
    private final String indexValue;

    public InquireRedisIndexContext(String indexName, String indexValue) {
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
