package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.InquireKeyResult;
import java.util.List;

/**
 *
 * @author aladdin
 */
public class InquireKeyResultImpl implements InquireKeyResult {

    private final int total;
    private final int pageSize;
    private final int pageNum;
    private final int pageIndex;
    private final List<String> resultList;

    public InquireKeyResultImpl(int total, int pageSize, int pageNum, int pageIndex, List<String> resultList) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.pageIndex = pageIndex;
        this.resultList = resultList;
    }
    
    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public List<String> getResultList() {
        return this.resultList;
    }

    @Override
    public boolean isEmpty() {
        return this.resultList.isEmpty();
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }
}
