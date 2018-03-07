package com.champion.dance.utils.page;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by lihongpeng on 7/17/2017.
 */
public class Paginator {

    private static final int DEFAULT_SLIDERS_COUNT = 7;

    /**
     * 分页大小
     */
    private int limit;
    /**
     * 页数
     */
    private int page;
    /**
     * 总记录数
     */
    private int totalCount;

    Paginator(int page, int limit, int totalCount) {
        super();
        this.limit = limit;
        this.totalCount = totalCount;
        this.page = computePageNo(page);
    }

    /**
     * 取得当前页。
     */
    int getPage() {
        return page;
    }

    int getLimit() {
        return limit;
    }

    /**
     * 取得总项数。
     *
     * @return 总项数
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 是否是首页（第一页），第一页页码为1
     *
     * @return 首页标识
     */
    boolean isFirstPage() {
        return page <= 1;
    }

    /**
     * 是否是最后一页
     *
     * @return 末页标识
     */
    boolean isLastPage() {
        return page >= getTotalPages();
    }

    int getPrePage() {
        if (isHasPrePage()) {
            return page - 1;
        } else {
            return page;
        }
    }

    int getNextPage() {
        if (isHasNextPage()) {
            return page + 1;
        } else {
            return page;
        }
    }

    /**
     * 是否有上一页
     *
     * @return 上一页标识
     */
    boolean isHasPrePage() {
        return (page - 1 >= 1);
    }

    /**
     * 是否有下一页
     *
     * @return 下一页标识
     */
    boolean isHasNextPage() {
        return (page + 1 <= getTotalPages());
    }

    /**
     * 开始行，可以用于oracle分页使用 (1-based)。
     */
    int getStartRow() {
        if (limit <= 0 || totalCount <= 0) {
            return 0;
        }
        return page > 0 ? (page - 1) * limit + 1 : 0;
    }

    /**
     * 结束行，可以用于oracle分页使用 (1-based)。
     */
    int getEndRow() {
        return page > 0 ? Math.min(limit * page, getTotalCount()) : 0;
    }

    /**
     * offset，计数从0开始，可以用于mysql分页使用(0-based)
     */
    int getOffset() {
        return page > 0 ? (page - 1) * limit : 0;
    }


    /**
     * 得到 总页数
     */
    int getTotalPages() {
        if (totalCount <= 0) {
            return 0;
        }
        if (limit <= 0) {
            return 0;
        }

        int count = totalCount / limit;
        if (totalCount % limit > 0) {
            count++;
        }
        return count;
    }

    private int computePageNo(int page) {
        return computePageNumber(page, limit, totalCount);
    }

    /**
     * 页码滑动窗口，并将当前页尽可能地放在滑动窗口的中间部位。
     */
    Integer[] getSlider() {
        return slider(DEFAULT_SLIDERS_COUNT);
    }

    /**
     * 页码滑动窗口，并将当前页尽可能地放在滑动窗口的中间部位。
     * 注意:不可以使用 getSlider(1)方法名称，因为在JSP中会与 getSlider()方法冲突，报exception
     *
     * @return x
     */
    private Integer[] slider(int slidersCount) {
        return generateLinkPageNumbers(getPage(), getTotalPages(), slidersCount);
    }

    private int computeLastPageNumber(int totalItems, int pageSize) {
        if (pageSize <= 0) {
            return 1;
        }
        int result = totalItems % pageSize == 0 ?
                totalItems / pageSize
                : totalItems / pageSize + 1;
        if (result <= 1){
            result = 1;
        }
        return result;
    }

    private int computePageNumber(int page, int pageSize, int totalItems) {
        if (page <= 1) {
            return 1;
        }
        if (Integer.MAX_VALUE == page
                || page > computeLastPageNumber(totalItems, pageSize)) { //last page
            return computeLastPageNumber(totalItems, pageSize);
        }
        return page;
    }

    private Integer[] generateLinkPageNumbers(int currentPageNumber, int lastPageNumber, int count) {

        int avg = count / 2;

        int startPageNumber = currentPageNumber - avg;
        if (startPageNumber <= 0) {
            startPageNumber = 1;
        }

        int endPageNumber = startPageNumber + count - 1;
        if (endPageNumber > lastPageNumber) {
            endPageNumber = lastPageNumber;
        }

        if (endPageNumber - startPageNumber < count) {
            startPageNumber = endPageNumber - count;
            if (startPageNumber <= 0) {
                startPageNumber = 1;
            }
        }

        final List<Integer> result = Lists.newArrayListWithCapacity(endPageNumber);

        for (int i = startPageNumber; i <= endPageNumber; i++) {
            result.add(i);
        }

        return result.toArray(new Integer[result.size()]);
    }
}
