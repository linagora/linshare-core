-- encipherement keypass is not used in linshare v0.3
alter table linshare_user drop column encipherment_key_pass;
alter table linshare_user add encipherment_key_pass bytea;

insert into linshare_version (id, description) values (2, 'Updated from v0.3 to v0.4');
