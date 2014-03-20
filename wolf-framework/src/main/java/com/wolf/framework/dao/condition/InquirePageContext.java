package com.wolf.framework.dao.condition;

/**
 * 分页查询条件
 *
 * @author aladdin
 */
public class InquirePageContext {

    private long pageSize = 10;
    private long pageIndex = 1;

    public final long getPageIndex() {
        return pageIndex;
    }

    public final void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex < 0 ? 1 : pageIndex;
    }

    public final long getPageSize() {
        return pageSize;
    }

    public final void setPageSize(long pageSize) {
        if (pageSize > 1000) {
            pageSize = 1000;
        } else {
            if (pageSize < 1) {
                pageSize = 10;
            }
        }
        this.pageSize = pageSize;
    }
}
