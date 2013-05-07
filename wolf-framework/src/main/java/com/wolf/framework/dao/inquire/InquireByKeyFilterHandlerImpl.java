package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public final class InquireByKeyFilterHandlerImpl<T extends Entity> implements InquireByKeyHandler<T> {

    private final InquireByKeyHandler<T> inquireByKeyHandler;

    public InquireByKeyFilterHandlerImpl(InquireByKeyHandler<T> inquireByKeyHandler) {
        this.inquireByKeyHandler = inquireByKeyHandler;
    }

    @Override
    public T inquireByKey(String keyValue) {
        return this.inquireByKeyHandler.inquireByKey(keyValue);
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        List<T> tList;
        switch (keyValues.size()) {
            case 0:
                //key值集合为空
                tList = new ArrayList<T>(0);
                break;
            case 1:
                //key数量为1
                tList = new ArrayList<T>(1);
                T t = this.inquireByKey(keyValues.get(0));
                tList.add(t);
                break;
            default:
                //key数量大于1
                tList = this.inquireByKeyHandler.inquireByKeys(keyValues);
                //排序
                List<T> sortList = new ArrayList<T>(tList.size());
                for (String keyValue : keyValues) {
                    for (T t1 : tList) {
                        if (t1.getKeyValue().equals(keyValue)) {
                            sortList.add(t1);
                            break;
                        }
                    }
                }
                tList = sortList;
        }
        return tList;
    }

    @Override
    public Map<String, String> inquireMapByKey(String keyValue) {
        return this.inquireByKeyHandler.inquireMapByKey(keyValue);
    }

    @Override
    public List<Map<String, String>> inquireMapByKeys(List<String> keyValues) {
        return this.inquireByKeyHandler.inquireMapByKeys(keyValues);
    }
}
