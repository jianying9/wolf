package com.wolf.framework.interceptor;

import com.wolf.framework.injecter.Injecter;
import java.util.List;

/**
 * 
 * @author jianying9
 */
public interface InterceptorContext {

    public void addInterceptor(final Interceptor interceptor);

    public List<Interceptor> getInterceptorList();
    
    public void inject(Injecter injecter);
}
