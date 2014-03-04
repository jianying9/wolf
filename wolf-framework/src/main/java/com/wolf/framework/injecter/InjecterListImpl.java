package com.wolf.framework.injecter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aladdin
 */
public final class InjecterListImpl implements Injecter {

    private final List<Injecter> injecterList = new ArrayList<Injecter>(10);
    
    public void addInjecter(Injecter injecter) {
        this.injecterList.add(injecter);
    }

    @Override
    public void parse(Object object) {
        for (Injecter injecter : injecterList) {
            injecter.parse(object);
        }
    }
}
