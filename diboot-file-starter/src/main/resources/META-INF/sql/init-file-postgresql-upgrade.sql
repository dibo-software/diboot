ALTER TABLE upload_file ADD COLUMN tenant_id bigint        not null default 0;
comment on column upload_file.tenant_id is '租户ID';
create index idx_upload_file_tenant on upload_file(tenant_id);