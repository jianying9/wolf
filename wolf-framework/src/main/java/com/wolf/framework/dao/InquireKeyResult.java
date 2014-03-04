package com.wolf.framework.dao;

import java.util.List;

/**
 *
 * @author aladdin
 */
public interface InquireKeyResult {

    public int getTotal();

    public int getPageSize();
    
    public int getPageNum();
    
    public int getPageIndex();

    public List<String> getResultList();

    public boolean isEmpty();
}
