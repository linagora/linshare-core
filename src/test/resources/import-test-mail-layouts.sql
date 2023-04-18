
-- Insert test mail layouts

insert into mail_layout (creation_date,description,domain_abstract_id,id,layout,messages_english,messages_french,messages_russian,modification_date,readonly,uuid,visible)
values
(NOW(),'Public root HTML layout 2',1,2,'','','','',NOW(),true,'b7b787ab-6305-458d-99fb-b84885178bd2',true),
(NOW(),'Public top layout',2,3,'','','','',NOW(),true,'fe8d86d9-ce27-4355-a539-26fad2b12621',true),
(NOW(),'Public sub layout',3,4,'','','','',NOW(),true,'782a6b5c-3991-442d-bb11-a5e74149e62a',true),
(NOW(),'Private root layout',1,5,'','','','',NOW(),true,'36481d51-442a-485f-b6c2-3674a0d2ebc0',false),
(NOW(),'Private top layout',2,6,'','','','',NOW(),true,'8e025cf3-d1fc-4fb4-bf01-6ca2ad800919',false),
(NOW(),'Private sub layout',3,7,'','','','',NOW(),true,'1385f33f-cb63-4426-a73e-224c1468363e',false);

