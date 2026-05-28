TRUNCATE TABLE schema_users.users RESTART IDENTITY;

DROP TABLE schema_users.users;

DELETE from schema_metrics.users WHERE id > 0;