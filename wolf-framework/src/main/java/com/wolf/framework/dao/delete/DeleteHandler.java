package com.wolf.framework.dao.delete;

import java.util.List;

/**
 *
 * @author aladdin
 */
public interface DeleteHandler {

    public void delete(String keyValue);

    public void batchDelete(List<String> keyValues);
}
