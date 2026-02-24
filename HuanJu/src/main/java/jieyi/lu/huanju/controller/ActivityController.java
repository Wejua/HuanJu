package jieyi.lu.huanju.controller;

import jieyi.lu.huanju.dto.ActivityListDTO;
import jieyi.lu.huanju.dto.ActivityRequest;
import jieyi.lu.huanju.dto.ActivityResponse;
import jieyi.lu.huanju.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 获取最新活动列表（分页）
     */
    @GetMapping("/latest")
    public ResponseEntity<ActivityResponse<ActivityListDTO>> getLatestActivities(
            @Valid ActivityRequest pageRequest) {
        log.info("请求最新活动列表");
        ActivityResponse<ActivityListDTO> result =
                activityService.getLatestActivities(pageRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * 按状态获取活动列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ActivityResponse<ActivityListDTO>> getActivitiesByStatus(
            @PathVariable String status,
            @Valid ActivityRequest pageRequest) {
        log.info("请求状态为 {} 的活动列表", status);
        ActivityResponse<ActivityListDTO> result =
                activityService.getActivitiesByStatus(status, pageRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索活动
     */
    @GetMapping("/search")
    public ResponseEntity<ActivityResponse<ActivityListDTO>> searchActivities(
            @RequestParam String keyword,
            @Valid ActivityRequest pageRequest) {
        log.info("搜索活动: keyword={}", keyword);
        ActivityResponse<ActivityListDTO> result =
                activityService.searchActivities(keyword, pageRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取活动详情（如果还需要）
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityListDTO> getActivityDetail(@PathVariable Long id) {
        // 这里需要实现
        return ResponseEntity.ok(null);
    }
}