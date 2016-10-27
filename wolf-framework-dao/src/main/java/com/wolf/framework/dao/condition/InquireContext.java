package com.wolf.framework.dao.condition;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询条件
 *
 * @author aladdin
 */
public final class InquireContext extends InquirePageContext {

    private final List<Condition> conditionList = new ArrayList<>(4);
    private final List<Order> orderList = new ArrayList<>(2);

    public void clearCondition() {
        this.conditionList.clear();
    }

    public void clearOrder() {
        this.orderList.clear();
    }

    public boolean hasCondition() {
        return this.conditionList.isEmpty() == false;
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
