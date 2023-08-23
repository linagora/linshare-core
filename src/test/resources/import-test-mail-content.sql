
-- Insert test mail contents

INSERT INTO mail_content (body,creation_date,description,domain_abstract_id,id,mail_content_type,messages_english,messages_french,messages_russian,messages_vietnamese,modification_date,readonly,subject,uuid,visible)
VALUES ('',NOW(),'root mail content public',1,50002,1,'','','','',NOW(),true,'', 'rootPublicContent',true),
       ('',NOW(),'top mail content public',2,50003,1,'','','','',NOW(),true,'',  'topPublicContent',true),
       ('',NOW(),'sub mail content public',3,50004,1,'','','','',NOW(),true,'',  'subPublicContent',true),
       ('',NOW(),'root mail content private',1,50005,1,'','','','',NOW(),true,'','rootPrivateContent',false),
       ('',NOW(),'top mail content private',2,50006,1,'','','','',NOW(),true,'', 'topPrivateContent',false),
       ('',NOW(),'sub mail content private',3,50007,1,'','','','',NOW(),true,'', 'subPrivateContent',false);