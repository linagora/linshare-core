CREATE TABLE mailing_list (
  id                  int8 NOT NULL, 
  domain_abstract_id int8 NOT NULL, 
  user_id            int8 NOT NULL, 
  is_public          bool NOT NULL, 
  identifier         varchar(255) NOT NULL, 
  description        text, 
  uuid               varchar(255) NOT NULL, 
  creation_date      timestamp(6) NOT NULL, 
  modification_date  timestamp(6) NOT NULL, 
  PRIMARY KEY (id));
CREATE TABLE mailing_list_contact (
  id                 int8 NOT NULL, 
  mailing_list_id    int8 NOT NULL, 
  mail               varchar(255) NOT NULL, 
  display            varchar(255) NOT NULL, 
  mailing_list_contact_index int4, 
  PRIMARY KEY (id));

ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li478123 FOREIGN KEY (user_id) REFERENCES users (account_id);
ALTER TABLE mailing_list ADD CONSTRAINT FKmailing_li335663 FOREIGN KEY (domain_abstract_id) REFERENCES domain_abstract (id);
ALTER TABLE mailing_list_contact ADD CONSTRAINT FKMailingLis595962 FOREIGN KEY (mailing_list_id) REFERENCES mailing_list (id);