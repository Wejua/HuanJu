package jieyi.lu.huanju.security;

import jieyi.lu.huanju.entity.Activity;
import jieyi.lu.huanju.entity.ActivityStatus;
import jieyi.lu.huanju.entity.User;
import jieyi.lu.huanju.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jieyi.lu.huanju.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component // 这个类只有一个构造方法，Spring Boot默认用这个生成 @Bean 单例。 如果有多个构造方法，要用 @Autowired 来指定用哪个
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository; // final 字段，线程安全, 不可变
    private final PasswordEncoder passwordEncoder;
    private final ActivityRepository activityRepository;

    @Override
    public void run(String... args) {

        // 1. 创建测试用户（如果没有）
        User admin = createTestUser();

        // 2. 创建测试活动
        createTestActivities(admin);
    }

    private User createTestUser() {
        // 创建测试用户（仅用于测试）
        if (!userRepository.existsByUsername("admin")) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setEmail("admin@example.com");
            user.setRole("ADMIN");
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("测试用户已创建 - 用户名: admin, 密码: admin123");
        }
        return userRepository.findByUsername("admin").orElse(null);
    }

    private void createTestActivities(User admin) {
        if (activityRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            // 活动1：即将开始的爬山活动
            Activity activity1 = new Activity();
            activity1.setTitle("周末白云山登山活动");
            activity1.setDetail("""
                    这周末我们去爬白云山，集合地点是南门，时间是早上9点。
                    
                    活动流程：
                    09:00 南门集合
                    09:30 开始登山
                    11:30 到达山顶，休息拍照
                    12:00 午餐（自带干粮）
                    14:00 下山
                    16:00 山脚集合，活动结束
                    
                    注意事项：
                    1. 穿运动鞋，带足够的水
                    2. 注意防晒
                    3. 量力而行，不要勉强
                    
                    期待大家的参与！这是一条很长的活动详情，用来测试截取摘要的功能。后面还有更多内容，确保超过100字，这样才能测试SUBSTRING功能是否正常工作。""");
            activity1.setLocation("白云山南门");
            activity1.setStartTime(now.plusDays(3).withHour(9).withMinute(0));
            activity1.setEndTime(now.plusDays(3).withHour(16).withMinute(0));
            activity1.setMaxParticipants(50);
            activity1.setStatus(ActivityStatus.UPCOMING);

            // 添加参加者（让admin参加）
            activity1.setAttendees(List.of(admin));

            activityRepository.save(activity1);
            log.info("创建测试活动: 周末白云山登山活动");

            // 活动2：进行中的技术分享会
            Activity activity2 = new Activity();
            activity2.setTitle("Spring Boot 技术分享会");
            activity2.setDetail("""
                    本次分享会主要讨论Spring Boot的最新特性，包括：
                    
                    1. Spring Boot 3.0 新特性
                    2. 虚拟线程的应用
                    3. 原生镜像支持
                    4. 可观测性增强
                    5. 实际项目中的最佳实践
                    
                    分享嘉宾：资深架构师 张三
                    
                    地点：科技园A座 3楼会议室
                    
                    这是一条很长的活动详情，用来测试截取摘要的功能。后面还有更多内容，确保超过100字，这样才能测试SUBSTRING功能是否正常工作。这是一个技术分享会，欢迎大家参加交流。""");
            activity2.setLocation("科技园A座 3楼会议室");
            activity2.setStartTime(now.minusHours(2));  // 2小时前开始
            activity2.setEndTime(now.plusHours(2));     // 2小时后结束
            activity2.setMaxParticipants(30);
            activity2.setStatus(ActivityStatus.ONGOING);

            activityRepository.save(activity2);
            log.info("创建测试活动: Spring Boot 技术分享会");

            // 活动3：已结束的聚餐活动
            Activity activity3 = new Activity();
            activity3.setTitle("季度团队聚餐");
            activity3.setDetail("""
                    为了庆祝项目成功上线，组织团队聚餐。
                    
                    地点：海底捞（天河店）
                    时间：晚上7点
                    
                    欢迎大家参加！这是一条很长的活动详情，用来测试截取摘要的功能。后面还有更多内容，确保超过100字，这样才能测试SUBSTRING功能是否正常工作。聚餐结束后可能还有KTV活动，自愿参加。""");
            activity3.setLocation("海底捞（天河店）");
            activity3.setStartTime(now.minusDays(5).withHour(19).withMinute(0));
            activity3.setEndTime(now.minusDays(5).withHour(22).withMinute(0));
            activity3.setMaxParticipants(20);
            activity3.setStatus(ActivityStatus.ENDED);

            activityRepository.save(activity3);
            log.info("创建测试活动: 季度团队聚餐");

            // 活动4：已取消的培训课程
            Activity activity4 = new Activity();
            activity4.setTitle("Java 高级特性培训");
            activity4.setDetail("""
                    原定于本周五的Java高级特性培训因讲师行程问题取消，将改期举行。
                    
                    给大家带来的不便敬请谅解。这是一条很长的活动详情，用来测试截取摘要的功能。后面还有更多内容，确保超过100字，这样才能测试SUBSTRING功能是否正常工作。新的培训时间确定后会另行通知。""");
            activity4.setLocation("培训中心 201教室");
            activity4.setStartTime(now.plusDays(2).withHour(14).withMinute(0));
            activity4.setEndTime(now.plusDays(2).withHour(17).withMinute(0));
            activity4.setMaxParticipants(25);
            activity4.setStatus(ActivityStatus.CANCELLED);

            activityRepository.save(activity4);
            log.info("创建测试活动: Java高级特性培训（已取消）");

            log.info("共创建 {} 条测试活动", activityRepository.count());
        } else {
            log.info("活动表已有数据，跳过初始化");
        }
    }
}
