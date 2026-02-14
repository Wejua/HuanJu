# Spring Boot 完整执行流程（时间线版）

## 第一部分：应用启动阶段
时间线中的步骤是"逻辑顺序"，物理上很多步骤确实可以同时执行——尤其是多个HTTP请求、多个数据库查询、后台异步任务。但Bean创建这一步，默认是单线程串行的，不能并行！

| 步骤 | 阶段 | 正在发生什么 |
|------|------|-------------|
| 第1步 | 启动 | 你按下 IDEA 的 Run 按钮 |
| 第2步 | 启动 | JVM 开始加载 HuanJuApplication.class |
| 第3步 | 启动 | main() 方法开始执行 |
| 第4步 | 启动 | SpringApplication.run() 被调用 |
| 第5步 | 启动 | 创建 StopWatch，开始计时 |
| 第6步 | 启动 | 设置 java.awt.headless=true |
| 第7步 | 启动 | 读取 spring.factories 文件 |
| 第8步 | 启动 | 创建 ApplicationContext（容器） |
| 第9步 | 启动 | 准备 Environment（环境） |
| 第10步 | 启动 | 读取 application.properties 文件 |
| 第11步 | 启动 | ⭐ 打印 Spring Boot Banner |
| 第12步 | 启动 | 创建 ApplicationContext 具体实现 |
| 第13步 | 启动 | 准备 BeanFactory |
| 第14步 | 启动 | 准备 BeanFactoryPostProcessor |
| 第15步 | 启动 | ⭐ 执行 @ComponentScan（扫描你的包） |
| 第16步 | 扫描 | ✓ 找到 AuthController |
| 第17步 | 扫描 | ✓ 找到 AuthService |
| 第18步 | 扫描 | ✓ 找到 UserRepository |
| 第19步 | 扫描 | ✓ 找到 SecurityConfig |
| 第20步 | 扫描 | ✓ 找到 JwtUtil |
| 第21步 | 扫描 | ✓ 找到 DataInitializer |
| 第22步 | 扫描 | ✓ 找到 User（@Entity，不创建Bean） |
| 第23步 | 启动 | 准备 BeanPostProcessor |
| 第24步 | 启动 | 初始化消息源 |
| 第25步 | 启动 | 初始化事件广播器 |
| 第26步 | 启动 | ⭐ onRefresh() - 准备 Web 容器 |
| 第27步 | 启动 | 创建 Tomcat 内嵌服务器 |
| 第28步 | 启动 | 设置 Tomcat 端口: 8080 |
| 第29步 | 启动 | 注册 Servlet 到 Tomcat |
| 第30步 | 启动 | 创建 DispatcherServlet |
| 第31步 | 启动 | 准备 WebApplicationContext |
| 第32步 | 启动 | 注册监听器 |
| 第33步 | 启动 | ⭐ finishBeanFactoryInitialization() - 创建所有单例 Bean |

### Bean 创建详细过程

| 步骤 | 类型 | 正在发生什么 |
|------|------|-------------|
| 第34步 | Bean | 创建 JwtUtil 实例 |
| 第35步 | Bean | ├─ 调用构造方法 JwtUtil() |
| 第36步 | Bean | ├─ @Value(${jwt.secret}) 注入: "mySecretKey" |
| 第37步 | Bean | ├─ @Value(${jwt.expiration}) 注入: 86400000 |
| 第38步 | Bean | └─ @Value(${jwt.issuer}) 注入: "HuanJu" |
| 第39步 | Bean | 创建 UserRepository 代理对象（JPA动态代理） |
| 第40步 | Bean | └─ 自动实现 findByUsername(), findByEmail(), existsByUsername()... |
| 第41步 | Bean | 创建 PasswordEncoder Bean（BCryptPasswordEncoder） |
| 第42步 | Bean | └─ 构造方法 BCryptPasswordEncoder() |
| 第43步 | Bean | 创建 SecurityConfig 实例 |
| 第44步 | Bean | ├─ 构造方法 SecurityConfig() |
| 第45步 | Bean | └─ 注入 UserRepository, JwtUtil |
| 第46步 | Bean | 创建 UserDetailsService Bean |
| 第47步 | Bean | └─ 执行 @Bean userDetailsService() 方法 |
| 第48步 | Bean | └─ 返回 Lambda (username -> { ... }) |
| 第49步 | Bean | 创建 SecurityFilterChain Bean |
| 第50步 | Bean | └─ 执行 @Bean filterChain() 方法 |
| 第51步 | Bean | └─ 调用 http.csrf().disable() |
| 第52步 | Bean | └─ 调用 http.sessionManagement().stateless() |
| 第53步 | Bean | └─ 配置 authorizeHttpRequests() |
| 第54步 | Bean | ├─ .requestMatchers("/api/login", "/api/login/test").permitAll() |
| 第55步 | Bean | └─ .anyRequest().authenticated() |
| 第56步 | Bean | └─ 调用 .addFilterBefore(JwtAuthenticationFilter, UsernamePasswordAuthenticationFilter) |
| 第57步 | Bean | └─ 创建 JwtAuthenticationFilter(jwtUtil, userDetailsService) |
| 第58步 | Bean | └─ http.build() 完成 |
| 第59步 | Bean | 创建 AuthService 实例 |
| 第60步 | Bean | ├─ 构造方法 AuthService() |
| 第61步 | Bean | └─ 注入 UserRepository, PasswordEncoder, JwtUtil |
| 第62步 | Bean | 创建 AuthController 实例 |
| 第63步 | Bean | ├─ 构造方法 AuthController() |
| 第64步 | Bean | └─ 注入 AuthService |
| 第65步 | Bean | 创建 DataInitializer 实例 |
| 第66步 | Bean | ├─ 构造方法 DataInitializer() |
| 第67步 | Bean | └─ 注入 UserRepository, PasswordEncoder |
| 第68步 | Bean | 检查所有依赖是否满足 |
| 第69步 | Bean | 所有单例 Bean 创建完成 |

### 启动完成阶段

| 步骤 | 阶段 | 正在发生什么 |
|------|------|-------------|
| 第70步 | 启动 | ⭐ finishRefresh() - 完成刷新 |
| 第71步 | 启动 | 启动 Tomcat |
| 第72步 | 启动 | ├─ 初始化连接器 |
| 第73步 | 启动 | ├─ 绑定端口 8080 |
| 第74步 | 启动 | └─ Tomcat 启动完成 |
| 第75步 | 日志 | "Tomcat started on port(s): 8080 (http)" |
| 第76步 | 启动 | ⭐ callRunners() - 执行 CommandLineRunner |
| 第77步 | 启动 | 找到 DataInitializer |
| 第78步 | 启动 | 执行 DataInitializer.run() |
| 第79步 | 启动 | ├─ userRepository.existsByUsername("admin") |
| 第80步 | 启动 | ├─ 数据库查询: SELECT COUNT(*) FROM users WHERE username='admin' |
| 第81步 | 启动 | ├─ 返回: false（用户不存在） |
| 第82步 | 启动 | ├─ 创建新 User 对象 |
| 第83步 | 启动 | ├─ user.setUsername("admin") |
| 第84步 | 启动 | ├─ user.setPassword(passwordEncoder.encode("admin123")) |
| 第85步 | 加密 | │  └─ BCrypt 加密完成 |
| 第86步 | 启动 | ├─ user.setEmail("admin@example.com") |
| 第87步 | 启动 | ├─ user.setRole("ADMIN") |
| 第88步 | 启动 | ├─ user.setEnabled(true) |
| 第89步 | 启动 | ├─ userRepository.save(user) |
| 第90步 | 数据库 | │  └─ INSERT INTO users ... |
| 第91步 | 启动 | └─ 控制台打印: "测试用户已创建 - 用户名: admin, 密码: admin123" |
| 第92步 | 启动 | 发布 ApplicationReadyEvent |
| 第93步 | 启动 | 打印启动时间 |
| 第94步 | 启动 | "Started HuanJuApplication in X.XXX seconds" |
| 第95步 | 启动 | 主线程进入等待状态，等待请求 |
| 第96步 | 启动 | ⭐ 启动完成！服务器就绪 |

---

## 第二部分：请求处理阶段（以 /api/login 为例）

| 步骤 | 阶段 | 正在发生什么 |
|------|------|-------------|
| 第1步 | 网络 | 客户端 POST /api/login (JSON: {"username":"admin","password":"admin123"}) |
| 第2步 | 网络 | 请求到达网卡 |
| 第3步 | TCP | 三次握手完成 |
| 第4步 | Tomcat | Acceptor 线程接收连接 |
| 第5步 | Tomcat | 分配处理线程 |
| 第6步 | Tomcat | 创建 HttpServletRequest 和 HttpServletResponse |
| 第7步 | Tomcat | 开始执行过滤器链 |

### 过滤器链执行

| 步骤 | 过滤器 | 正在发生什么 |
|------|--------|-------------|
| 第8步 | 过滤 | 1. WebAsyncManagerIntegrationFilter |
| 第9步 | 过滤 | 2. SecurityContextPersistenceFilter |
| 第10步 | 过滤 | 3. HeaderWriterFilter |
| 第11步 | 过滤 | 4. CorsFilter（检查跨域） |
| 第12步 | 过滤 | 5. CsrfFilter（已禁用，直接通过） |
| 第13步 | 过滤 | 6. LogoutFilter（不是登出请求，通过） |
| 第14步 | 过滤 | ⭐ 7. JwtAuthenticationFilter（你的过滤器！） |
| 第15步 | 过滤 | ├─ request.getHeader("Authorization") → null |
| 第16步 | 过滤 | ├─ 没有 token，放行 |
| 第17步 | 过滤 | └─ chain.doFilter() |
| 第18步 | 过滤 | 8. UsernamePasswordAuthenticationFilter |
| 第19步 | 过滤 | ├─ 尝试获取 username/password 参数 → null |
| 第20步 | 过滤 | └─ 继续 |
| 第21步 | 过滤 | 9. DefaultLoginPageGeneratingFilter |
| 第22步 | 过滤 | 10. DefaultLogoutPageGeneratingFilter |
| 第23步 | 过滤 | 11. BasicAuthenticationFilter |
| 第24步 | 过滤 | 12. RequestCacheAwareFilter |
| 第25步 | 过滤 | 13. SecurityContextHolderAwareRequestFilter |
| 第26步 | 过滤 | 14. AnonymousAuthenticationFilter |
| 第27步 | 过滤 | 15. SessionManagementFilter |
| 第28步 | 过滤 | 16. ExceptionTranslationFilter |
| 第29步 | 过滤 | 17. AuthorizationFilter（检查权限） |
| 第30步 | 过滤 | └─ /api/login 已放行，通过 |

### Controller 执行

| 步骤 | 阶段 | 正在发生什么 |
|------|------|-------------|
| 第31步 | MVC | DispatcherServlet 开始处理 |
| 第32步 | MVC | 遍历所有 HandlerMapping |
| 第33步 | MVC | └─ RequestMappingHandlerMapping 找到 AuthController.login() |
| 第34步 | MVC | 获取 HandlerAdapter |
| 第35步 | MVC | 执行拦截器 preHandle() |
| 第36步 | MVC | ⭐ 执行 Controller 方法 |
| 第37步 | 业务 | AuthController.login() 开始 |
| 第38步 | 业务 | ├─ @RequestBody 解析 JSON → LoginRequest 对象 |
| 第39步 | JSON | │  └─ Jackson 反序列化 |
| 第40步 | 业务 | ├─ 调用 authService.login(loginRequest) |
| 第41步 | 业务 | AuthService.login() 开始 |
| 第42步 | 业务 | ├─ userRepository.findByUsername("admin") |
| 第43步 | JPA | │  └─ 创建查询: SELECT * FROM users WHERE username=? |
| 第44步 | 连接 | │  └─ 从连接池获取数据库连接 |
| 第45步 | 网络 | │  └─ 发送 SQL 到 MySQL |
| 第46步 | 数据库 | │  └─ MySQL 执行查询 |
| 第47步 | 数据库 | │  └─ 返回结果集 |
| 第48步 | JPA | │  └─ 结果集 → User 实体对象 |
| 第49步 | 业务 | ├─ User 对象存在，继续 |
| 第50步 | 业务 | ├─ passwordEncoder.matches("admin123", 存储的密码) |
| 第51步 | 加密 | │  └─ BCrypt 验证密码 |
| 第52步 | 加密 | │  └─ 验证成功 ✅ |
| 第53步 | 业务 | ├─ jwtUtil.generateToken("admin") |
| 第54步 | JWT | │  ├─ 创建 JWT Builder |
| 第55步 | JWT | │  ├─ .withSubject("admin") |
| 第56步 | JWT | │  ├─ .withIssuer("HuanJu") |
| 第57步 | JWT | │  ├─ .withIssuedAt(当前时间) |
| 第58步 | JWT | │  ├─ .withExpiresAt(当前时间 + 86400000) |
| 第59步 | JWT | │  ├─ .sign(Algorithm.HMAC256("mySecretKey")) |
| 第60步 | JWT | │  └─ 返回 Token |
| 第61步 | 业务 | ├─ 创建 LoginResponse 对象 |
| 第62步 | 业务 | └─ 返回 LoginResponse |
| 第63步 | 业务 | AuthController.login() 收到响应 |
| 第64步 | 业务 | └─ return ResponseEntity.ok(response) |

### 响应返回

| 步骤 | 阶段 | 正在发生什么 |
|------|------|-------------|
| 第65步 | MVC | Controller 执行完成 |
| 第66步 | MVC | 执行拦截器 postHandle() |
| 第67步 | MVC | 处理返回值 |
| 第68步 | JSON | ⭐ 将 LoginResponse 对象 → JSON |
| 第69步 | JSON | ├─ getToken() → Token 字符串 |
| 第70步 | JSON | ├─ getUsername() → "admin" |
| 第71步 | JSON | ├─ getRole() → "ADMIN" |
| 第72步 | JSON | └─ 生成 JSON 字符串 |
| 第73步 | MVC | 添加响应头 |
| 第74步 | MVC | ├─ Content-Type: application/json |
| 第75步 | MVC | └─ Status: 200 OK |
| 第76步 | MVC | 完成处理，交回给过滤器链 |
| 第77步 | 过滤 | 过滤器链继续执行（返回路径） |
| 第78步 | 过滤 | 各过滤器 postProcess 处理 |
| 第79步 | 过滤 | SecurityContext 清理 |
| 第80步 | Tomcat | 将响应数据写入 Socket |
| 第81步 | 网络 | 数据包通过网络传输 |
| 第82步 | 客户端 | 收到 HTTP 响应 |
| 第83步 | 客户端 | 解析 JSON |
| 第84步 | 客户端 | 显示结果 |
| 第85步 | 完成 | 请求处理完成 |

---

## 第三部分：带 Token 的请求处理阶段（以 /api/user/me 为例）

| 步骤 | 阶段 | 正在发生什么 |
|------|------|-------------|
| 第1步 | 网络 | 客户端 GET /api/user/me (带 Token) |
| 第2步 | 网络 | 请求到达 |
| 第3步 | Tomcat | 接收请求 |
| 第4步 | 过滤 | JwtAuthenticationFilter 执行 |
| 第5步 | 过滤 | ├─ 获取 Authorization: "Bearer eyJhbGciOiJ..." |
| 第6步 | 过滤 | ├─ 提取 token |
| 第7步 | 过滤 | ├─ jwtUtil.extractUsername(token) → "admin" |
| 第8步 | 过滤 | ├─ userDetailsService.loadUserByUsername("admin") |
| 第9步 | JPA | │  └─ 查询数据库获取最新用户信息 |
| 第10步 | 过滤 | ├─ jwtUtil.validateToken(token, userDetails) → true |
| 第11步 | 过滤 | ├─ 创建 UsernamePasswordAuthenticationToken |
| 第12步 | 过滤 | ├─ SecurityContextHolder.getContext().setAuthentication(...) |
| 第13步 | 过滤 | └─ 继续执行 |
| 第14步 | MVC | DispatcherServlet 处理 |
| 第15步 | MVC | 找到 UserController.getCurrentUser() |
| 第16步 | 业务 | ⭐ @AuthenticationPrincipal UserDetails userDetails 被注入 |
| 第17步 | 业务 | └─ 直接从 SecurityContext 获取用户信息 |
| 第18步 | 业务 | └─ return "当前登录用户: admin" |
| 第19步 | MVC | 返回响应 |
| 第20步 | 客户端 | 收到: "当前登录用户: admin" |

---

## 核心步骤总结表

| 阶段 | 关键步骤 | 作用 |
|------|---------|------|
| 启动 | 第1-15步 | 初始化 Spring 容器 |
| 启动 | 第16-22步 | ⭐ 扫描你的组件 |
| 启动 | 第34-69步 | ⭐ 创建所有 Bean |
| 启动 | 第70-75步 | 启动 Tomcat |
| 启动 | 第76-91步 | ⭐ 执行 DataInitializer（创建 admin 用户） |
| 请求 | 第8-30步 | ⭐ 执行过滤器链（JWT 检查） |
| 请求 | 第31-36步 | DispatcherServlet 分发 |
| 请求 | 第37-64步 | ⭐ 执行你的业务逻辑 |
| 请求 | 第68-72步 | JSON 转换 |
| 请求 | 第77-84步 | 返回响应 |

---

[启动阶段]

主线程: 第1-33步 [==========准备容器、扫描组件==========]
↓ ↓ ↓ ↓
主线程: 第34-69步 [创建JwtUtil][创建UserRepo][创建AuthService]... ← 串行！一个接一个
↓ ↓ ↓ ↓
Tomcat线程: [启动Tomcat ==========] ← 和Bean创建后期可并行
↓ ↓ ↓ ↓
主线程: 第76-91步 [DataInitializer执行] ← 串行
↓ ↓ ↓ ↓
启动完成: ✅
====================================================================================================
[运行阶段]

Tomcat线程池（默认200个线程）:
↓ ↓ ↓ ↓
请求1 → 线程1: [处理请求1 ==========]
请求2 → 线程2: [处理请求2 ==========] ← 并行！
请求3 → 线程3: [处理请求3 ==========] ← 并行！
请求4 → 线程4: [处理请求4 ==========] ← 并行！
... (最多200个请求同时处理)
↓ ↓ ↓ ↓
数据库连接池（默认10个连接）:
↓ ↓ ↓ ↓
连接1: [查询] ← 被线程1用
连接2: [查询] ← 被线程2用
连接3: [查询] ← 被线程3用
... (10个查询可同时进行)
↓ ↓ ↓ ↓
@Async线程池（默认8个线程）:
↓ ↓ ↓ ↓
线程A: [发送邮件====] ← 和请求处理并行
线程B: [生成报表==] ← 和请求处理并行
线程C: [清理缓存=] ← 和请求处理并行