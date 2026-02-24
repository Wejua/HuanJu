package jieyi.lu.huanju.repository;

import jieyi.lu.huanju.dto.ActivityListDTO;
import jieyi.lu.huanju.entity.Activity;
import jieyi.lu.huanju.entity.ActivityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // 查询到所有活动后，每个活动都会访问 attendees 属性触发一次中间表查询（N+1问题），截取detail字段也可以在这里完成，可以使用JPQL一次查完
    // SUBSTRING(a.detail, 1, 100), 是截取detail字段前100个字符
    @Query("""
        SELECT new jieyi.lu.huanju.dto.ActivityListDTO(
            a.id,
            a.title,
            SUBSTRING(a.detail, 1, 100),
            a.location,
            a.startTime,
            a.endTime,
            a.maxParticipants,
            a.status,
            SIZE(a.attendees)
        )
        FROM Activity a
        ORDER BY a.startTime DESC
        """)
    Page<ActivityListDTO> findActivityList(Pageable pageable);

    // 1. 分页查询所有活动（按开始时间倒序）
    Page<Activity> findAllByOrderByStartTimeDesc(Pageable pageable);

    // 2. 按状态分页查询
    Page<Activity> findByStatusOrderByStartTimeDesc(ActivityStatus status, Pageable pageable);

    // 3. 按时间范围分页查询
    Page<Activity> findByStartTimeBetweenOrderByStartTimeDesc(
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 4. 复杂查询：搜索标题或地点
    @Query("SELECT a FROM Activity a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Activity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 5. 查询即将开始的活动
    @Query("SELECT a FROM Activity a WHERE " +
            "a.startTime > :now AND a.status = 'UPCOMING' " +
            "ORDER BY a.startTime ASC")
    Page<Activity> findUpcoming(@Param("now") LocalDateTime now, Pageable pageable);

    // 6. 查询进行中的活动
    @Query("SELECT a FROM Activity a WHERE " +
            "a.startTime <= :now AND a.endTime >= :now AND a.status = 'ONGOING'")
    Page<Activity> findOngoing(@Param("now") LocalDateTime now, Pageable pageable);
}
