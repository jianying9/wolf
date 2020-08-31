package com.wolf.framework.interceptor;

import com.wolf.framework.injecter.Injecter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author jianying9
 */
public class InterceptorContextImpl implements InterceptorContext {

    private final List<Interceptor> interceptorList;

    public InterceptorContextImpl() {
        this.interceptorList = new ArrayList(0);
    }

    @Override
    public void addInterceptor(Interceptor interceptor) {
        this.interceptorList.add(interceptor);
    }

    @Override
    public List<Interceptor> getInterceptorList() {
        InterceptorAscComparator interceptorAscComparator = new InterceptorAscComparator();
        Collections.sort(this.interceptorList, interceptorAscComparator);
        return this.interceptorList;
    }

    @Override
    public void inject(Injecter injecter) {
        for (Interceptor interceptor : this.interceptorList) {
            injecter.parse(interceptor);
        }
    }

    private final class InterceptorAscComparator implements Comparator<Interceptor> {

        @Override
        public int compare(Interceptor o1, Interceptor o2) {
            int result = 0;
            if (o1.getSort() > o2.getSort()) {
                result = 1;
            } else if (o1.getSort() < o2.getSort()) {
                result = -1;
            }
            return result;
        }

    }

}
