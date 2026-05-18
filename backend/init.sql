-- Create databases
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS delivery_db;
CREATE DATABASE IF NOT EXISTS tracking_db;
CREATE DATABASE IF NOT EXISTS admin_db;

-- Create application user and grant permissions
CREATE USER IF NOT EXISTS 'smartcourier_user'@'%' IDENTIFIED BY 'smartcourier_db_pass_2026';
GRANT ALL PRIVILEGES ON auth_db.* TO 'smartcourier_user'@'%';
GRANT ALL PRIVILEGES ON delivery_db.* TO 'smartcourier_user'@'%';
GRANT ALL PRIVILEGES ON tracking_db.* TO 'smartcourier_user'@'%';
GRANT ALL PRIVILEGES ON admin_db.* TO 'smartcourier_user'@'%';
FLUSH PRIVILEGES;