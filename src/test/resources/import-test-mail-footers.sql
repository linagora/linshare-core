
-- Insert test mail footers

insert into mail_footer (creation_date,description,domain_abstract_id,id,footer,messages_english,messages_french,messages_russian,modification_date,readonly,uuid,visible)
values
(NOW(),'Public root HTML footer 2',1,2,'','','','',NOW(),true,'f871dfec-21f8-4f7f-99f2-eb2d4afb7ad6',true),
(NOW(),'Public top footer',2,3,'','','','',NOW(),true,'2bd363a3-6431-41e6-8093-96f71a7f5fc4',true),
(NOW(),'Public sub footer',3,4,'','','','',NOW(),true,'e585cc07-7fa8-4cb8-87a6-a768ee037a56',true),
(NOW(),'Private root footer',1,5,'','','','',NOW(),true,'5886053d-b58f-4424-a131-2fc27b2e5d56',false),
(NOW(),'Private top footer',2,6,'','','','',NOW(),true,'20d3f480-117a-4d39-af72-9ef7ea98afb4',false),
(NOW(),'Private sub footer',3,7,'','','','',NOW(),true,'c34d689c-7d6a-4552-8d8b-052f06d2c854',false);

