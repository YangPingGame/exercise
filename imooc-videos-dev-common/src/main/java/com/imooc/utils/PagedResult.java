package com.imooc.utils;

import java.util.List;

/**
 * @author 1819014975
 * @Title: PagedResult
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/18 11:37
 */
public class PagedResult {
    private int page;  //当前页数
    private int total;  //总页数
    private long records; //总记录数
    private List<?> rows;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
