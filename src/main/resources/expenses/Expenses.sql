drop table if exists department cascade;
create table department (
  did         integer       not null,
  dname       varchar(64)   not null,
  primary key (did),
  unique (dname)
);

drop table if exists employee cascade;
create table employee(
  empid       integer       not null,
  empname     varchar(64)   not null,
  did         integer       not null,
  primary key (empid),
  foreign key (did)
    references department(did)
);

drop table if exists expense cascade;
create table expense(
  expid       integer       not null,
  expname     varchar(64)   not null,
  primary key (expid)
);

drop table if exists expense_submission cascade;
create table expense_submission(
  sid         integer       not null,
  empid       integer       not null,
  sdate       date          not null,
  primary key (sid),
  foreign key (empid)
    references employee(empid)
);

drop table if exists expense_submission_details cascade;
create table expense_submission_details(
  id          integer       not null,
  sid         integer       not null,
  expid       integer       not null,
  amount      decimal(10,2) not null,
  primary key (id),
  foreign key (sid)
    references expense_submission(sid),
  foreign key (expid)
    references expense(expid)
);
