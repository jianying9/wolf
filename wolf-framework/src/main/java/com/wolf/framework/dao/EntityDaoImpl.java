package com.wolf.framework.dao;

import com.wolf.framework.dao.condition.Condition;
import com.wolf.framework.dao.condition.InquireContext;
import com.wolf.framework.dao.condition.OperateTypeEnum;
import com.wolf.framework.dao.delete.DeleteHandler;
import com.wolf.framework.dao.inquire.CountByConditionHandler;
import com.wolf.framework.dao.inquire.InquireByConditionHandler;
import com.wolf.framework.dao.inquire.InquireByKeyHandler;
import com.wolf.framework.dao.inquire.InquireKeyByConditionHandler;
import com.wolf.framework.dao.inquire.InquireKeyResultImpl;
import com.wolf.framework.dao.inquire.InquireResultImpl;
import com.wolf.framework.dao.insert.InsertHandler;
import com.wolf.framework.dao.update.UpdateHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aladdin
 */
public class EntityDaoImpl<T extends Entity> implements EntityDao<T> {

    private final InsertHandler<T> insertHandler;
    private final UpdateHandler updateHandler;
    private final DeleteHandler deleteHandler;
    private final InquireByKeyHandler<T> inquireByKeyHandler;
    private final InquireByConditionHandler<T> inquireByConditionHandler;
    private final InquireKeyByConditionHandler inquireKeyByConditionHandler;
    private final CountByConditionHandler countByConditionHandler;

    public EntityDaoImpl(InsertHandler insertHandler, UpdateHandler updateHandler, DeleteHandler deleteHandler, InquireByKeyHandler<T> inquireByKeyHandler, InquireByConditionHandler<T> inquireByConditionHandler, InquireKeyByConditionHandler inquireKeyByConditionHandler, CountByConditionHandler countByConditionHandler) {
        this.insertHandler = insertHandler;
        this.updateHandler = updateHandler;
        this.deleteHandler = deleteHandler;
        this.inquireByKeyHandler = inquireByKeyHandler;
        this.inquireByConditionHandler = inquireByConditionHandler;
        this.inquireKeyByConditionHandler = inquireKeyByConditionHandler;
        this.countByConditionHandler = countByConditionHandler;
    }

    @Override
    public T inquireByKey(String keyValue) {
        return this.inquireByKeyHandler.inquireByKey(keyValue);
    }

    @Override
    public List<T> inquireByKeys(List<String> keyValues) {
        return this.inquireByKeyHandler.inquireByKeys(keyValues);
    }

    @Override
    public InquireResult<T> inquirePageByCondition(InquireContext inquireContext) {
        int total = this.countByConditionHandler.count(inquireContext.getConditionList());
        int pageIndex = inquireContext.getPageIndex();
        int pageSize = inquireContext.getPageSize();
        int pageNum = total / pageSize;
        if (total % pageSize > 0) {
            pageNum++;
        }
        List<T> tList;
        if ((pageIndex - 1) * pageSize < total) {
            //当前页存在
            tList = this.inquireByConditionHandler.inquireByConditon(inquireContext);
        } else {
            //当前也不存在
            tList = new ArrayList<T>(0);
        }
        InquireResult<T> inquireResult = new InquireResultImpl<T>(total, pageSize, pageNum, pageIndex, tList);
        return inquireResult;
    }

    @Override
    public String insert(Map<String, String> entityMap) {
        return this.insertHandler.insert(entityMap);
    }

    @Override
    public T insertAndInquire(Map<String, String> entityMap) {
        String keyValue = this.insert(entityMap);
        return this.inquireByKey(keyValue);
    }

    @Override
    public void batchInsert(List<Map<String, String>> entityMapList) {
        int num = entityMapList.size();
        switch (num) {
            case 0:
                break;
            case 1:
                this.insertHandler.insert(entityMapList.get(0));
                break;
            default:
                this.insertHandler.batchInsert(entityMapList);
        }
    }

    @Override
    public String update(Map<String, String> entityMap) {
        return this.updateHandler.update(entityMap);
    }

    @Override
    public void batchUpdate(List<Map<String, String>> entityMapList) {
        int num = entityMapList.size();
        switch (num) {
            case 0:
                break;
            case 1:
                this.updateHandler.update(entityMapList.get(0));
                break;
            default:
                this.updateHandler.batchUpdate(entityMapList);
        }
    }

    @Override
    public T updateAndInquire(Map<String, String> entityMap) {
        String keyValue = this.updateHandler.update(entityMap);
        return this.inquireByKeyHandler.inquireByKey(keyValue);
    }

    @Override
    public void delete(String keyValue) {
        this.deleteHandler.delete(null);
    }

    @Override
    public void batchDelete(List<String> keyValues) {
        int num = keyValues.size();
        switch (num) {
            case 0:
                break;
            case 1:
                this.deleteHandler.delete(keyValues.get(0));
                break;
            default:
                this.deleteHandler.batchDelete(keyValues);
        }
    }

    @Override
    public InquireKeyResult inquirePageKeysByCondition(InquireContext inquireContext) {
        int total = this.countByConditionHandler.count(inquireContext.getConditionList());
        int pageIndex = inquireContext.getPageIndex();
        int pageSize = inquireContext.getPageSize();
        int pageNum = total / pageSize;
        if (total % pageSize > 0) {
            pageNum++;
        }
        List<String> keyList;
        if ((pageIndex - 1) * pageSize < total) {
            //当前页存在
            keyList = this.inquireKeyByConditionHandler.inquireByConditon(inquireContext);
        } else {
            //当前也不存在
            keyList = new ArrayList<String>(0);
        }
        InquireKeyResult inquireKeyResult = new InquireKeyResultImpl(total, pageSize, pageNum, pageIndex, keyList);
        return inquireKeyResult;
    }

    @Override
    public List<T> inquireByColumn(String columnName, String columnValue) {
        InquireContext inquireContext = new InquireContext();
        Condition condition = new Condition(columnName, OperateTypeEnum.EQUAL, columnValue);
        inquireContext.addCondition(condition);
        return this.inquireByCondition(inquireContext);
    }

    @Override
    public List<T> inquireByColumns(String columnName, String columnValue, String columnNameTwo, String columnValueTwo) {
        InquireContext inquireContext = new InquireContext();
        Condition condition = new Condition(columnName, OperateTypeEnum.EQUAL, columnValue);
        inquireContext.addCondition(condition);
        condition = new Condition(columnNameTwo, OperateTypeEnum.EQUAL, columnValueTwo);
        inquireContext.addCondition(condition);
        return this.inquireByCondition(inquireContext);
    }

    @Override
    public List<T> inquireByCondition(InquireContext inquireContext) {
        //设置pageIndex:0代表无分页
        inquireContext.setPageIndex(0);
        return this.inquireByConditionHandler.inquireByConditon(inquireContext);
    }

    @Override
    public List<String> inquireKeysByColumn(String columnName, String columnValue) {
        InquireContext inquireContext = new InquireContext();
        Condition condition = new Condition(columnName, OperateTypeEnum.EQUAL, columnValue);
        inquireContext.addCondition(condition);
        return this.inquireKeysByCondition(inquireContext);
    }

    @Override
    public List<String> inquireKeysByColumns(String columnName, String columnValue, String columnNameTwo, String columnValueTwo) {
        InquireContext inquireContext = new InquireContext();
        Condition condition = new Condition(columnName, OperateTypeEnum.EQUAL, columnValue);
        inquireContext.addCondition(condition);
        condition = new Condition(columnNameTwo, OperateTypeEnum.EQUAL, columnValueTwo);
        inquireContext.addCondition(condition);
        return this.inquireKeysByCondition(inquireContext);
    }

    @Override
    public List<String> inquireKeysByCondition(InquireContext inquireContext) {
        //设置pageIndex:0代表无分页
        inquireContext.setPageIndex(0);
        return this.inquireKeyByConditionHandler.inquireByConditon(inquireContext);
    }

    @Override
    public int count(String columnName, String columnValue) {
        InquireContext inquireContext = new InquireContext();
        Condition condition = new Condition(columnName, OperateTypeEnum.EQUAL, columnValue);
        inquireContext.addCondition(condition);
        return this.count(inquireContext);
    }

    @Override
    public int count(String columnName, String columnValue, String columnNameTwo, String columnValueTwo) {
        InquireContext inquireContext = new InquireContext();
        Condition condition = new Condition(columnName, OperateTypeEnum.EQUAL, columnValue);
        inquireContext.addCondition(condition);
        condition = new Condition(columnNameTwo, OperateTypeEnum.EQUAL, columnValueTwo);
        inquireContext.addCondition(condition);
        return this.count(inquireContext);
    }

    @Override
    public int count(InquireContext inquireContext) {
        return this.countByConditionHandler.count(inquireContext.getConditionList());
    }
}
