package com.wolf.framework.dao;

import java.util.Map;

/**
 * 响应数据接口
 *
 * @author aladdin
 */
public interface Entity {

    /**
     * 实体信息转换成Map
     *
     * @return
     */
    public Map<String, String> toMap();
}
