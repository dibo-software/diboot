ALTER TABLE ${SCHEMA}.upload_file ADD tenant_id bigint        not null default 0;
execute sp_addextendedproperty 'MS_Description', N'租户ID','SCHEMA', '${SCHEMA}', 'table', dictionary, 'column', 'tenant_id';
create nonclustered index idx_upload_file_tenant on ${SCHEMA}.upload_file(tenant_id);