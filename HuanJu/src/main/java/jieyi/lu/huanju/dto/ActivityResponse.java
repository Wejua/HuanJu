package jieyi.lu.huanju.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class ActivityResponse<T> {
    private List<T> content;          // 当前页的数据
    private int page;                  // 当前页码
    private int size;                  // 每页大小
    private long totalElements;        // 总记录数
    private int totalPages;            // 总页数
    private boolean first;              // 是否是第一页
    private boolean last;               // 是否是最后一页

    public static <T> ActivityResponse<T> from(Page<T> page) {
        return new ActivityResponse<>(
                page.getContent(),
                page.getNumber() + 1,  // 转为从1开始的页码
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
