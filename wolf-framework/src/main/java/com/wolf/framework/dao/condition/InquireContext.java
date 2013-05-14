package com.wolf.framework.dao.condition;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询条件
 *
 * @author aladdin
 */
public final class InquireContext {

    private int pageSize = 10;
    private int pageIndex = 1;
    private final List<Condition> conditionList = new ArrayList<Condition>(6);
    private final List<Order> orderList = new ArrayList<Order>(4);

    public void clearCondition() {
        this.conditionList.clear();
    }
    
    public void clearOrder() {
        this.orderList.clear();
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex < 0 ? 0 : pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize > 1000) {
            pageSize = 1000;
        } else {
            if (pageSize < 1) {
                pageSize = 10;
            }
        }
        this.pageSize = pageSize;
    }

    public boolean hasCondition() {
        return this.conditionList.isEmpty() ? false : true;
    }

    public void addCondition(Condition condition) {
        this.conditionList.add(condition);
    }

    public void addCondition(List<Condition> conditionList) {
        this.conditionList.addAll(conditionList);
    }

    public List<Condition> getConditionList() {
        return conditionList;
    }

    public Condition getCondition(String columnName) {
        Condition result = null;
        for (Condition condition : conditionList) {
            if (condition.getColumnName().equals(columnName)) {
                result = condition;
                break;
            }
        }
        return result;
    }
    
    public void addOrder(Order order) {
        this.orderList.add(order);
    }
    
    public List<Order> getOrderList() {
        return this.orderList;
    }
}
