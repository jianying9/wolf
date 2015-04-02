package com.wolf.framework.dao.condition;

/**
 *
 * @author aladdin
 */
public final class Order {

    private final String columnName;
    private final OrderType orderType;

    public Order(String columnName, OrderType orderType) {
        this.columnName = columnName;
        this.orderType = orderType;
    }

    public String getColumnName() {
        return columnName;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
