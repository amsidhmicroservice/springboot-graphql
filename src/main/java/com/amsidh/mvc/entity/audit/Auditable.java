package com.amsidh.mvc.entity.audit;

public interface Auditable {
    Audit getAudit();

    void setAudit(Audit audit);
}
