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
        this.filterMap.put(FilterType.ESCAPE, new EscapeFilterImpl());
        this.filterMap.put(FilterType.SECURITY, new SecurityFilterImpl());
    }
    private final Map<FilterType, Filter> filterMap = new EnumMap<>(FilterType.class);

    @Override
    public Filter getFilter(final FilterType filterType) {
        return this.filterMap.get(filterType);
    }
}
