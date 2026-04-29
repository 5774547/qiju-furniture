package com.qiju.furniture.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 数据列表 */
    private List<T> records;

    /** 总数 */
    private long total;

    /** 当前页码 */
    private int page;

    /** 每页条数 */
    private int size;

    /** 总页数 */
    private long pages;

    /** 是否有下一页 */
    private boolean hasNext;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        long pages = (total + size - 1) / size;
        return new PageResult<>(records, total, page, size, pages, page < pages);
    }
}
