package com.amsidh.mvc.entity.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditListener {

    @PrePersist
    void preCreate(Auditable auditable) {
        Audit audit = auditable.getAudit();
        if (Objects.isNull(audit)) {
            audit = Audit.builder()
                    .createdBy("System")
                    .createdOn(LocalDateTime.now())
                    .build();
            auditable.setAudit(audit);
        }

    }

    @PreUpdate
    void preUpdate(Auditable auditable) {
        Audit audit = auditable.getAudit();
        if (Objects.isNull(audit)) {
            audit = Audit.builder()
                    .createdBy("System")
                    .createdOn(LocalDateTime.now())
                    .build();
            auditable.setAudit(audit);
        } else {
            audit = Audit.builder()
                    .lastModifiedBy("System")
                    .lastModifiedOn(LocalDateTime.now())
                    .build();
            auditable.setAudit(audit);
        }
    }

}
