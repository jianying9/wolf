package com.wolf.framework.dao.inquire;

import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.condition.OperateTypeEnum;
import com.wolf.framework.dao.parser.ColumnHandler;
import com.wolf.framework.data.DataTypeEnum;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aladdin
 */
public abstract class AbstractInquireConditionHandler {

    private final List<ColumnHandler> columnHandlerList;

    public AbstractInquireConditionHandler(List<ColumnHandler> columnHandlerList) {
        this.columnHandlerList = columnHandlerList;
    }

    private ColumnHandler getColumnHandler(String columnName) {
        ColumnHandler result = null;
        for (ColumnHandler columnHandler : this.columnHandlerList) {
            if (columnName.equals(columnHandler.getColumnName())) {
                result = columnHandler;
                break;
            }
        }
        return result;
    }

    private String getNextDayMilliseconds(String dayStartMilliseconds) {
        long start = Long.parseLong(dayStartMilliseconds);
        long nextStart = start + 86400000;
        return Long.toString(nextStart);
    }

    protected List<Condition> filterCondition(List<Condition> conditionList) {
        List<Condition> resultList;
        ColumnHandler columnHandler;
        DataTypeEnum dataTypeEnum;
        OperateTypeEnum operateTypeEnum;
        Condition newCondition;
        String columnName;
        String columnValue;
        if (conditionList.isEmpty()) {
            resultList = conditionList;
        } else {
            List<Condition> existConditionList = new ArrayList<Condition>(conditionList.size());
            for (Condition condition : conditionList) {
                columnName = condition.getColumnName();
                columnHandler = this.getColumnHandler(columnName);
                if (columnHandler != null) {
                    dataTypeEnum = columnHandler.getDataHandler().getDataTypeEnum();
                    if (dataTypeEnum == DataTypeEnum.DATE) {
                        operateTypeEnum = condition.getOperateTypeEnum();
                        columnValue = condition.getColumnValue();
                        switch (operateTypeEnum) {
                            case EQUAL:
                                newCondition = new Condition(columnName, OperateTypeEnum.GREATER_OR_EQUAL, columnValue);
                                existConditionList.add(newCondition);
                                columnValue = this.getNextDayMilliseconds(columnValue);
                                newCondition = new Condition(columnName, OperateTypeEnum.LESS, columnValue);
                                existConditionList.add(newCondition);
                                break;
                            case LESS_OR_EQUAL:
                                columnValue = this.getNextDayMilliseconds(columnValue);
                                newCondition = new Condition(columnName, OperateTypeEnum.LESS, columnValue);
                                existConditionList.add(newCondition);
                                break;
                            case NOT_EQUAL:
                                newCondition = new Condition(columnName, OperateTypeEnum.LESS, columnValue);
                                existConditionList.add(newCondition);
                                columnValue = this.getNextDayMilliseconds(columnValue);
                                newCondition = new Condition(columnName, OperateTypeEnum.GREATER_OR_EQUAL, columnValue);
                                existConditionList.add(newCondition);
                                break;
                            case GREATER:
                                columnValue = this.getNextDayMilliseconds(columnValue);
                                newCondition = new Condition(columnName, OperateTypeEnum.GREATER_OR_EQUAL, columnValue);
                                existConditionList.add(newCondition);
                                break;
                            default:
                                existConditionList.add(condition);
                        }
                    } else {
                        existConditionList.add(condition);
                    }
                }
            }
            resultList = existConditionList;
        }
        return resultList;
    }

    protected void filterCondition(InquireContext inquireContext) {
        List<Condition> conditionList = this.filterCondition(inquireContext.getConditionList());
        inquireContext.clearCondition();
        inquireContext.addCondition(conditionList);
    }
}
