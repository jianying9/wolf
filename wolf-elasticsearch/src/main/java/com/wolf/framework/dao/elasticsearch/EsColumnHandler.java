package com.wolf.framework.dao.elasticsearch;

import com.wolf.framework.dao.ColumnHandler;

/**
 *
 * @author jianying9
 */
public interface EsColumnHandler extends  ColumnHandler {

    public boolean isAnalyzer();
    
    public EsColumnDataType getEsColumnDataType();

}
