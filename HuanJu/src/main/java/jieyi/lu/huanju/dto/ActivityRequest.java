package jieyi.lu.huanju.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class ActivityRequest {

    @Min(value = 1, message = "页码从1开始")
    private int page = 1;  // 默认第1页

    @Min(value = 1, message = "每页大小至少为1")
    private int size = 10;  // 默认每页10条

    private String sortBy = "startTime";  // 默认按开始时间排序
    private String sortDir = "desc";      // 默认降序（最新的在前）

    // 转换为 Spring Data 的 Pageable
    public Pageable toPageable() {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return PageRequest.of(page - 1, size, sort);  // page从0开始
    }
}

