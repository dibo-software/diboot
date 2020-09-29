ALTER TABLE ${SCHEMA}.dictionary ADD COLUMN tenant_id NUMBER(20)           default 0  not null;
comment on column ${SCHEMA}.dictionary.tenant_id is '租户ID';
CREATE INDEX idx_directory_tenant on ${SCHEMA}.dictionary(tenant_id);