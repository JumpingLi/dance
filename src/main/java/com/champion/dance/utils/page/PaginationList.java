package com.champion.dance.utils.page;

import com.github.pagehelper.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihongpeng on 7/17/2017.
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class PaginationList<E> extends ArrayList<E> {

    private Paginator paginator;

    public PaginationList(List list) {
        //noinspection unchecked
        int currentPageNum;
        int declarePageSize;
        int totalCount;
        if (list instanceof Page) {
            final Page page = (Page) list;
            currentPageNum = page.getPageNum();
            declarePageSize = page.getPageSize();
            totalCount = (int) page.getTotal();
        } else {
            currentPageNum = 1;
            declarePageSize = list.size();
            totalCount = list.size();
        }

        this.paginator = new Paginator(currentPageNum, declarePageSize, totalCount);
    }
}
