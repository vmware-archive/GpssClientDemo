create role gpssuser login password 'gprocks@2020';

grant connect on database gpdemo to gpssuser;

grant ALL on database gpdemo to gpssuser;

CREATE EXTENSION gpss;

create schema gpss_demo;

alter user gpssuser createexttable;


set role 'sridhar';

create table gpss_demo.simpleseries_1
    (
        comments text,
        atime timestamp
        without time zone) 
    distributed randomly;

/* test insert */
insert into gpss_demo.simpleseries_1( comments, atime)
select comm, ts
from (SELECT generate_series(1,10) AS id, md5(random()::text) AS comm, now() as ts) s;

select count(*) from gpss_demo.simpleseries_1 
