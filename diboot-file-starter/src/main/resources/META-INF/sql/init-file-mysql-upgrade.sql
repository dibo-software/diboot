ALTER TABLE upload_file ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER uuid;
CREATE INDEX idx_upload_file_tenant on upload_file(tenant_id);