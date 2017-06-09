package com.wolf.framework.interceptor;

import com.wolf.framework.injecter.Injecter;
import java.util.ArrayList;
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
        return this.interceptorList;
    }

    @Override
    public void inject(Injecter injecter) {
        for (Interceptor interceptor : this.interceptorList) {
            injecter.parse(interceptor);
        }
    }
}
