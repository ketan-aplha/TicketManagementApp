package com.Library.Management.tenant;

import org.hibernate.cfg.MultiTenancySettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

@Component
public class CurrentTenantIdentifierResolverImpl
        implements CurrentTenantIdentifierResolver<Long>, HibernatePropertiesCustomizer {

    private static final Logger log = LoggerFactory.getLogger(CurrentTenantIdentifierResolverImpl.class);
    private final TenantContext tenantContext;

    public CurrentTenantIdentifierResolverImpl(TenantContext tenantContext) {
        this.tenantContext = tenantContext;
    }

    @Override
    public Long resolveCurrentTenantIdentifier() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return TenantContext.BOOTSTRAP_TENANT;
        }
        Long tenantId = tenantContext.getTenantId();
        if (tenantId != null) {
            return tenantId;
        }
        return TenantContext.BOOTSTRAP_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        log.info("Configuring Hibernate discriminator multi-tenancy");
        hibernateProperties.put(MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
