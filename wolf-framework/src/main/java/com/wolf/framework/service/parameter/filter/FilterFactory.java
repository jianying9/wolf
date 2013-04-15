package com.wolf.framework.service.parameter.filter;

/**
 * 过滤对象工厂类
 *
 * @author aladdin
 */
public interface FilterFactory {

    public Filter getFilter(final FilterTypeEnum filterTypeEnum);
}
