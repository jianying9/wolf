package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import java.util.List;

/**
 *
 * @author aladdin
 * @param <T>
 */
public interface InquireByKeyHandler<T extends Entity> {

    public T inquireByKey(String keyValue);
    
    public List<T> inquireByKeys(List<String> keyValues);
}
