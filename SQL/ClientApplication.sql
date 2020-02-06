
UPDATE user 'gpss'@'%' ;

UPDATE user SET authentication_string=PASSWORD("pgrocks!") WHERE User='gpss';

FLUSH PRIVILEGES;

grant all privileges on gpss_meta.* to 'gpss'@'%';

SET ROLE 'gpss';


CREATE TABLE gpss_meta.hour_glass
(
    id INT NOT NULL
    AUTO_INCREMENT KEY,
	job_name varchar
    (256) NOT NULL,
	job_signal varchar
    (4) DEFAULT 'stop' NOT NULL
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;


insert into gpss_meta.hour_glass (job_name) values('simpleseries_1');
/* Make the job active */
update gpss_meta.hour_glass set job_signal='run' where job_name='simpleseries_1'

