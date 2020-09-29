ALTER TABLE dictionary ADD COLUMN tenant_id bigint        not null default 0;
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'tenant_id';
create nonclustered index idx_directory_tenant on ${SCHEMA}.dictionary(tenant_id);