package com.wolf.framework.lucene;

import java.util.List;
import org.apache.lucene.document.Document;

/**
 *
 * @author aladdin
 */
public class DocumentResultImpl implements DocumentResult {

    private int total = 0;
    private int pageSize = 10;
    private String nextPageIndex = "";
    private List<Document> resultList;

    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public String getNextPageIndex() {
        return this.nextPageIndex;
    }

    @Override
    public List<Document> getResultList() {
        return this.resultList;
    }

    void setTotal(int total) {
        this.total = total;
    }

    void setNextPageIndex(String nextPageIndex) {
        this.nextPageIndex = nextPageIndex;
    }

    void setResultList(List<Document> resultList) {
        this.resultList = resultList;
    }

    @Override
    public boolean isEmpty() {
        return this.resultList.isEmpty();
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
