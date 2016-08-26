package com.wolf.framework.dao;

import java.util.List;

/**
 *
 * @author aladdin
 */
public interface InquireResult<T extends Entity> {

    public long getTotal();

    public long getPageSize();
    
    public long getPageNum();
    
    public long getPageIndex();

    public List<T> getResultList();

    public boolean isEmpty();
}
