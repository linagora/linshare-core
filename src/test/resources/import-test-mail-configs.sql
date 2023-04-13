
-- Insert test mail configs


insert into mail_config (creation_date,domain_abstract_id,id,mail_layout_id,modification_date,name,readonly,uuid,visible)
    values
    (NOW(),1,2,1,NOW(),'Second mail config',true,'f8313520-da11-11ed-afa1-0242ac120002',true),
    (NOW(),3,3,1,NOW(),'Third mail config',true,'08c1c3c8-da12-11ed-afa1-0242ac120002',true);
