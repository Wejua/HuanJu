package jieyi.lu.huanju.service;

import jieyi.lu.huanju.dto.ActivityListDTO;
import jieyi.lu.huanju.dto.ActivityResponse;
import jieyi.lu.huanju.dto.ActivityRequest;
import jieyi.lu.huanju.entity.Activity;
import jieyi.lu.huanju.entity.ActivityStatus;
import jieyi.lu.huanju.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    /**
     * 分页查询最新活动
     */
    public ActivityResponse<ActivityListDTO> getLatestActivities(ActivityRequest pageRequest) {
        log.info("查询活动列表: page={}, size={}, sortBy={}, sortDir={}",
                pageRequest.getPage(), pageRequest.getSize(),
                pageRequest.getSortBy(), pageRequest.getSortDir());

        // 执行分页查询
        // Page<Activity> activityPage = activityRepository.findAllByOrderByStartTimeDesc(pageRequest.toPageable());
        // 转换
        // Page<ActivityListDTO> dtoPage = activityPage.map(ActivityListDTO::from);

        // 执行分页查询(优化方案)
        Page<ActivityListDTO> dtoPage = activityRepository.findActivityList(
                pageRequest.toPageable());

        // 返回分页结果
        return ActivityResponse.from(dtoPage);
    }

    /* 缓存查询
        @Cacheable(value = "activityList",
               key = "#pageRequest.page + '-' + #pageRequest.size",
               unless = "#result == null")
    public ActivityResponse<ActivityListDTO> getLatestActivities(ActivityRequest pageRequest) {
        Page<ActivityListDTO> dtoPage = activityRepository.findActivityList(
            pageRequest.toPageable());
        return ActivityResponse.from(dtoPage);
    }

    @CacheEvict(value = "activityList", allEntries = true)
    @Transactional
    public void createActivity(ActivityCreateDTO createDTO) {
        // 创建活动后清空列表缓存
    }
    */

    /**
     * 按状态分页查询
     */
    public ActivityResponse<ActivityListDTO> getActivitiesByStatus(
            String status, ActivityRequest pageRequest) {
        // 状态转换（需要添加枚举解析）
        // 这里简化处理，实际应该有更完善的枚举转换
        Page<Activity> activityPage = activityRepository.findByStatusOrderByStartTimeDesc(
                ActivityStatus.valueOf(status), pageRequest.toPageable());

        // 用 Lambda 调用构造方法转换
        Page<ActivityListDTO> dtoPage = activityPage.map(activity ->
                new ActivityListDTO(
                        activity.getId(),
                        activity.getTitle(),
                        activity.getDetail() != null && activity.getDetail().length() > 100
                                ? activity.getDetail().substring(0, 100) + "..."
                                : activity.getDetail(),
                        activity.getLocation(),
                        activity.getStartTime(),
                        activity.getEndTime(),
                        activity.getMaxParticipants(),
                        activity.getStatus(),
                        activity.getAttendees() != null ? activity.getAttendees().size() : 0
                )
        );
        return ActivityResponse.from(dtoPage);
    }

    /**
     * 搜索活动
     */
    public ActivityResponse<ActivityListDTO> searchActivities(
            String keyword, ActivityRequest pageRequest) {
        Page<Activity> activityPage = activityRepository.searchByKeyword(
                keyword, pageRequest.toPageable());

        // 用 Lambda 调用构造方法转换
        Page<ActivityListDTO> dtoPage = activityPage.map(activity ->
                new ActivityListDTO(
                        activity.getId(),
                        activity.getTitle(),
                        activity.getDetail() != null && activity.getDetail().length() > 100
                                ? activity.getDetail().substring(0, 100) + "..."
                                : activity.getDetail(),
                        activity.getLocation(),
                        activity.getStartTime(),
                        activity.getEndTime(),
                        activity.getMaxParticipants(),
                        activity.getStatus(),
                        activity.getAttendees() != null ? activity.getAttendees().size() : 0
                )
        );

        return ActivityResponse.from(dtoPage);
    }
}
