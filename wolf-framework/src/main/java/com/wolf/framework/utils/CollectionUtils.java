package com.wolf.framework.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jianying9
 */
public final class CollectionUtils {
    
    public static <O extends Object> List<O> removeRepeated(List<O> list) {
        List<O> newList = new ArrayList(list.size());
        Set<O> set = new HashSet(list.size());
        for (O o : list) {
            if(set.add(o)) {
                newList.add(o);
            }
        }
        return newList;
    }
}
