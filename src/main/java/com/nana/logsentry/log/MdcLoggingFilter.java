package com.nana.logsentry.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            MDC.put(MdcKeys.TIMESTAMP, Instant.now().toString());
            MDC.put(MdcKeys.TRACE_ID, UUID.randomUUID().toString());
            MDC.put(MdcKeys.USER_ID, resolveUserId());
            MDC.put(MdcKeys.CLIENT_IP, resolveClientIp(request));
            MDC.put(MdcKeys.USER_AGENT, request.getHeader("User-Agent"));
            MDC.put(MdcKeys.METHOD, request.getMethod());
            MDC.put(MdcKeys.URI, request.getRequestURI());

            chain.doFilter(request, response);

        } finally {
            MDC.clear(); // 메모리 누수 방지
        }
    }

    /**
     * resolveUserId() -> 현재 인증된 사용자의 ID를 추출하는 메서드
     * Spring Security의 SecurityContext에서 Authentication 객체를 꺼내 사용자 ID를 반환
     *
     * - 인증되지 않은 경우에는 "anonymous" 반환
     * - 예외 발생 시에는 "unknown" 반환
     *
     * ⚠️ 회원 시스템이 도입되면, 이 메서드에서 실제 사용자 ID 또는 유저 객체에서 고유 식별자를 꺼내도록 수정
     */

    private String resolveUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // 인증 객체가 없거나, 인증되지 않았거나, 익명 사용자인 경우
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
                return "anonymous"; // 인증되지 않은 사용자
            }

            // 인증된 사용자의 ID 반환 (기본적으로 username)
            return auth.getName();
        } catch (Exception e) {
            return "unknown"; // 예외 발생 시 fallback 값
        }
    }

    private String resolveClientIp(HttpServletRequest request) { //	요청을 보낸 클라이언트의 IP 추출
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0] : request.getRemoteAddr();
    }
}
