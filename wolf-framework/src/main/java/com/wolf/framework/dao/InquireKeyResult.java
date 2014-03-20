package com.wolf.framework.dao;

import java.util.List;

/**
 *
 * @author aladdin
 */
public interface InquireKeyResult {

    public long getTotal();

    public long getPageSize();
    
    public long getPageNum();
    
    public long getPageIndex();

    public List<String> getResultList();

    public boolean isEmpty();
}
