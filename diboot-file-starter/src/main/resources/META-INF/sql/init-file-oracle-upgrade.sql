ALTER TABLE ${SCHEMA}.upload_file ADD COLUMN tenant_id NUMBER(20)           default 0  not null;
comment on column ${SCHEMA}.upload_file.tenant_id is '租户ID';
CREATE INDEX idx_upload_file_tenant on ${SCHEMA}.upload_file(tenant_id);