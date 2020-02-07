-- root domain 1, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=100,
	quota=2300,
	quota_warning=2000,
	max_file_size=10
	where id=1;

-- topdomain 2, DOMAIN_QUOTA
update quota set
	current_value=1096,
	last_value=500,
	quota=1900,
	quota_warning=1800,
	max_file_size=5,
	default_max_file_size=5
	where id=@quota_my_domain_id;

-- topdomain 2, CONTAINER_QUOTA - USER
update quota set
	current_value=496,
	last_value=0,
	quota=1900,
	quota_warning=1300,
	max_file_size=5,
	default_max_file_size=5,
	batch_modification_date=now()
	where id=@quota_on_my_domain_container_user_id;

-- topdomain 2, CONTAINER_QUOTA - THREAD
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
