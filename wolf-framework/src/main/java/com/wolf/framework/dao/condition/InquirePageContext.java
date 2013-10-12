package com.wolf.framework.dao.condition;

/**
 * 分页查询条件
 *
 * @author aladdin
 */
public class InquirePageContext {

    private int pageSize = 10;
    private int pageIndex = 1;

    public final int getPageIndex() {
        return pageIndex;
    }

    public final void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex < 0 ? 1 : pageIndex;
    }

    public final int getPageSize() {
        return pageSize;
    }

    public final void setPageSize(int pageSize) {
        if (pageSize > 50) {
            pageSize = 50;
        } else {
            if (pageSize < 1) {
                pageSize = 10;
            }
        }
        this.pageSize = pageSize;
    }
}
