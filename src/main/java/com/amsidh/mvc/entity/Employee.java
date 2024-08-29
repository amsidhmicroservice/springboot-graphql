package com.amsidh.mvc.entity;

import com.amsidh.mvc.entity.audit.Audit;
import com.amsidh.mvc.entity.audit.AuditListener;
import com.amsidh.mvc.entity.audit.Auditable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EMPLOYEE")
@EntityListeners(AuditListener.class)
public class Employee extends AbstractEntity implements Auditable {
    @Embedded
    private Audit audit;
    private String name;
    private String emailId;
}
