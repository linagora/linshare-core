
-- Insert test mail configs


insert into mail_config (creation_date,domain_abstract_id,id,mail_layout_id,modification_date,name,readonly,uuid,visible)
    values
    (NOW(),1,2,1,NOW(),'Second mail config',true,'f8313520-da11-11ed-afa1-0242ac120002',true),
    (NOW(),2,3,1,NOW(),'top mail confog pblic',true,'a0f1675c-dd32-11ed-b5ea-0242ac120002',true),
    (NOW(),3,4,1,NOW(),'Third mail config',true,'08c1c3c8-da12-11ed-afa1-0242ac120002',true),
    (NOW(),1,5,1,NOW(),'root mail config private',true,'90553b64-dd2b-11ed-afa1-0242ac120002',false),
    (NOW(),2,6,1,NOW(),'top mail config private',true,'98807ba0-dd2b-11ed-afa1-0242ac120002',false),
    (NOW(),3,7,1,NOW(),'sub mail config private',true,'a059f50e-dd2b-11ed-afa1-0242ac120002',false);
