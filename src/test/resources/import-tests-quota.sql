-- root domain 1, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=100,
	quota=2300,
	quota_warning=2000,
	max_file_size=10
	where id=1;

-- MyDomain, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=500,
	quota=1900,
	quota_warning=1800,
	max_file_size=5,
	default_max_file_size=5
	where id=@quota_my_domain_id;

-- MyDomain, CONTAINER_QUOTA - USER Jane (11)
update quota set
	current_value=496,
	last_value=0,
	quota=1900,
	quota_warning=1300,
	max_file_size=5,
	default_max_file_size=5,
	batch_modification_date=now()
	where id=@quota_account_jane_id;
	
-- MyDomain, CONTAINER_QUOTA - USER John (10)
update quota set
	current_value=900,
	last_value=100,
	quota=1500,
	quota_warning=1000,
	max_file_size=6,
	default_max_file_size=6,
	batch_modification_date=now()
	where id=@quota_account_jhon_id;
	
-- CONTAINER_QUOTA - USER
update quota set
	current_value=496,
	last_value=0,
	quota=1900,
	quota_warning=1300,
	max_file_size=5,
	default_max_file_size=5,
	batch_modification_date=now()
	where id=@quota_on_my_domain_container_user_id;
	
-- MyDomain, CONTAINER_QUOTA - WORKGROUP
update quota set
	current_value=900,
	last_value=200,
	quota=2000,
	quota_warning=1500,
	max_file_size=5,
	default_max_file_size=5,
	default_account_quota=2000,
	account_quota=2000,
	batch_modification_date=now()
	where id=@quota_on_my_domain_container_workgroup_id;



-- MyDomain, CONTAINER_QUOTA - WORKGROUP (20)
update quota set
	current_value=900,
	last_value=200,
	quota=2000,
	quota_warning=1500,
	max_file_size=5,
	default_max_file_size=5,
	default_account_quota=2000,
	account_quota=2000,
	batch_modification_date=now()
	where id=@quota_workgroup_20_id;
	
-- guestdomain 2, CONTAINER_QUOTA - WORKGROUP (21)
update quota set
	current_value=500,
	last_value=200,
	quota=1300,
	quota_warning=1500,
	max_file_size=6,
	default_max_file_size=6,
	default_account_quota=2000,
	account_quota=2000,
	batch_modification_date=now()
	where id=@quota_workgroup_21_id;


