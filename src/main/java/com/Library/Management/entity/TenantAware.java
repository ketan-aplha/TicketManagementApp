package com.Library.Management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TenantId;

@MappedSuperclass
@Getter
@Setter
public abstract class TenantAware {

    @TenantId
    @Column(name = "organisation_id", nullable = false, updatable = false)
    private Long organisationId;
}
