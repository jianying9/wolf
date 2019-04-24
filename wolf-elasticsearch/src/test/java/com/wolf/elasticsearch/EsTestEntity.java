package com.wolf.elasticsearch;

import com.wolf.framework.dao.ColumnType;
import com.wolf.framework.dao.Entity;
import com.wolf.framework.dao.elasticsearch.annotation.EsColumnConfig;
import com.wolf.framework.dao.elasticsearch.annotation.EsEntityConfig;
import java.util.List;

/**
 *
 * @author jianying9
 */
@EsEntityConfig(
        table = "es_test"
)
public class EsTestEntity implements Entity {

    @EsColumnConfig(columnType = ColumnType.KEY, desc = "展示id")
    private long showId;

    @EsColumnConfig(desc = "状态")
    private boolean enabled;

    @EsColumnConfig(desc = "", analyzer = true)
    private String content;

    @EsColumnConfig(desc = "标题")
    private String title;

    @EsColumnConfig(desc = "奖金")
    private double money;

    @EsColumnConfig(desc = "")
    private List<String> voteIdList;

    public long getShowId() {
        return showId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public double getMoney() {
        return money;
    }

    public List<String> getVoteIdList() {
        return voteIdList;
    }

}
