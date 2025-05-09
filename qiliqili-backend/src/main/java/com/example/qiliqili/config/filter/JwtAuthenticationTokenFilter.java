package com.example.qiliqili.config.filter;

import com.example.qiliqili.pojo.User;
import com.example.qiliqili.service.impl.user.UserDetailsImpl;
import com.example.qiliqili.utils.JwtUtil;
import com.example.qiliqili.utils.RedisUtil;




import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * token 认证过滤器，任何请求访问服务器都会先被这里拦截验证token合法性
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
//        log.info("token:{}", token);

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer " ) || request.getRequestURI().startsWith("/user/account/login")) {
            // 通过开放接口过滤器后，如果没有可解析的token就放行
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        // 解析token
        boolean verifyToken = jwtUtil.verifyToken(token);
        if (!verifyToken) {
//            log.error("当前token已过期");
            response.addHeader("message", "not login"); // 设置响应头信息，给前端判断用
            response.setStatus(403);
//            throw new AuthenticationException("当前token已过期");
            return;
        }
        String userId = JwtUtil.getSubjectFromToken(token);
        String role = JwtUtil.getClaimFromToken(token, "role");

        // 从redis中获取用户信息
        User user = redisUtil.getObject("security:" + role + ":" + userId, User.class);

        if (user == null) {
//            log.error("用户未登录");
            response.addHeader("message", "not login"); // 设置响应头信息，给前端判断用
            response.setStatus(403);
//            throw new AuthenticationException("用户未登录");
            return;
        }

        // 存入SecurityContextHolder，这里建议只供读取uid用，其中的状态等非静态数据可能不准，所以建议redis另外存值
        UserDetailsImpl loginUser = new UserDetailsImpl(user);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 放行
        filterChain.doFilter(request, response);
    }
}
