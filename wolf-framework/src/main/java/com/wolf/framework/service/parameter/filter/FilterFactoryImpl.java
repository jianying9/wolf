package com.wolf.framework.service.parameter.filter;

import java.util.EnumMap;
import java.util.Map;

/**
 * 过滤对象工厂类
 *
 * @author aladdin
 */
public final class FilterFactoryImpl implements FilterFactory {

    public FilterFactoryImpl() {
        this.filterMap.put(FilterTypeEnum.ESCAPE, new EscapeFilterImpl());
        this.filterMap.put(FilterTypeEnum.SECURITY, new SecurityFilterImpl());
    }
    private final Map<FilterTypeEnum, Filter> filterMap = new EnumMap<FilterTypeEnum, Filter>(FilterTypeEnum.class);

    @Override
    public Filter getFilter(final FilterTypeEnum filterTypeEnum) {
        return this.filterMap.get(filterTypeEnum);
    }
}
