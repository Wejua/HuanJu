package jieyi.lu.huanju.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ 没有token，放行"); // 有可能是不需要验证的请求，如登录
            chain.doFilter(request, response); // 如果不调用 chain.doFilter()，请求就卡在这里，永远不会到达 Controller！
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        System.out.println("用户名: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // 判断 getAuthentication 是为了防止重复认证
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // 这里调用的就是 SecurityConfig 中的 userDetailsService 中的 Lambda
            System.out.println("UserDetails: " + userDetails);

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // userDetails: 用户信息 , null是密码（已认证，设为null）， userDetails.getAuthorities(): 权限列表
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // 设置请求详情（IP、Session等）
                SecurityContextHolder.getContext().setAuthentication(authToken); // 存入 SecurityContext！这是最关键的一步！
                System.out.println("✅ 认证成功，权限: " + userDetails.getAuthorities());
            } else {
                System.out.println("❌ token验证失败");
            }
        }

        chain.doFilter(request, response); // 如果不调用 chain.doFilter()，请求就卡在这里，永远不会到达 Controller！
    }
}
