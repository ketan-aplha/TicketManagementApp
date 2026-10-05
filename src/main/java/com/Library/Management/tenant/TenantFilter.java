package com.Library.Management.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    public static final String ORGANISATION_HEADER = "X-Organisation-Id";
    public static final String ORGANISATION_PARAM = "organisationId";
    public static final String SESSION_ORGANISATION_ID = "ORGANISATION_ID";

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);
    private final TenantContext tenantContext;

    public TenantFilter(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            Long tenantId = resolveTenantId(request);
            if (tenantId != null) {
                tenantContext.setTenantId(tenantId);
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute(SESSION_ORGANISATION_ID, tenantId);
                }
                log.info("Resolved organisation tenant {}", tenantId);
            }
            filterChain.doFilter(request, response);
        } finally {
            tenantContext.clear();
        }
    }

    private Long resolveTenantId(HttpServletRequest request) {
        Long fromHeader = parseTenantId(request.getHeader(ORGANISATION_HEADER));
        if (fromHeader != null) {
            return fromHeader;
        }
        Long fromParam = parseTenantId(request.getParameter(ORGANISATION_PARAM));
        if (fromParam != null) {
            return fromParam;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object fromSession = session.getAttribute(SESSION_ORGANISATION_ID);
            if (fromSession instanceof Long tenantId) {
                return tenantId;
            }
        }
        return null;
    }

    private Long parseTenantId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            log.warn("Ignoring invalid organisation id {}", value);
            return null;
        }
    }
}
