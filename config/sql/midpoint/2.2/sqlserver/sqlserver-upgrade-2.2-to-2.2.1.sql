CREATE INDEX iParent ON m_task (parent);

ALTER TABLE m_sync_situation_description ADD fullFlag BIT;
ALTER TABLE m_shadow ADD fullSynchronizationTimestamp DATETIME2;
ALTER TABLE m_task ADD expectedTotal BIGINT;
ALTER TABLE m_assignment ADD disableReason NVARCHAR(255);
ALTER TABLE m_focus ADD disableReason NVARCHAR(255);
ALTER TABLE m_shadow ADD disableReason NVARCHAR(255);
ALTER TABLE m_audit_delta ADD context NVARCHAR(MAX);
ALTER TABLE m_audit_delta ADD returns NVARCHAR(MAX);
ALTER TABLE m_operation_result ADD context NVARCHAR(MAX);
ALTER TABLE m_operation_result ADD returns NVARCHAR(MAX);


CREATE INDEX iAncestorDepth ON m_org_closure (ancestor_id, ancestor_oid, depthValue);
