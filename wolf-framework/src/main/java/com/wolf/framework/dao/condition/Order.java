package com.wolf.framework.dao.condition;

/**
 *
 * @author aladdin
 */
public final class Order {

    private final String columnName;
    private final OrderTypeEnum orderType;

    public Order(String columnName, OrderTypeEnum orderType) {
        this.columnName = columnName;
        this.orderType = orderType;
    }

    public String getColumnName() {
        return columnName;
    }

    public OrderTypeEnum getOrderType() {
        return orderType;
    }
}
