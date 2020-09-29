ALTER TABLE dictionary ADD COLUMN tenant_id bigint        not null default 0;
comment on column dictionary.tenant_id is '租户ID';
create index idx_directory_tenant on dictionary(tenant_id);