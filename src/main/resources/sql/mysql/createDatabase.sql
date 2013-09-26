-- Default script for database creation

DROP DATABASE IF EXISTS `linshare`;
DROP DATABASE IF EXISTS `linshare_data`;

CREATE DATABASE `linshare` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE DATABASE `linshare_data` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;


GRANT ALL PRIVILEGES ON `linshare`.* TO 'linshare'@'localhost';
GRANT ALL PRIVILEGES ON `linshare_data`.* TO 'linshare'@'localhost';

FLUSH PRIVILEGES;
