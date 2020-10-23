ALTER TABLE dictionary ADD COLUMN tenant_id bigint NOT NULL DEFAULT 0 COMMENT '租户ID' AFTER id;
CREATE INDEX idx_directory_tenant on dictionary(tenant_id);