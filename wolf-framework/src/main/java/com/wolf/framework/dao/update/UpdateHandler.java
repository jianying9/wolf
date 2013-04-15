package com.wolf.framework.dao.update;

import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public interface UpdateHandler {

    public String update(Map<String, String> entityMap);

    public void batchUpdate(List<Map<String, String>> entityMapList);
}
