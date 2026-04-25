CREATE USER IF NOT EXISTS 'customer_service_user'@'%' IDENTIFIED BY '0715980980';
GRANT ALL PRIVILEGES ON customer_service.* TO 'customer_service_user'@'%';
FLUSH PRIVILEGES;
