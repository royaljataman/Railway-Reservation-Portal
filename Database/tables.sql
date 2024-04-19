ALTER DATABASE dbms_project SET DEFAULT_TRANSACTION_ISOLATION TO 'serializable';;

drop table if exists train_data; 
create table train_data( 
    train_number integer not null, 
    doj varchar(10) not null,  
    ac_max integer not null, 
    sl_max integer not null, 
    primary key (train_number, doj));;

drop table if exists seats_filled; 
create table seats_filled(
    train_no integer not null,
    doj varchar(10) not null,
    ac_filled integer not null,
    sl_filled integer not null,
    ac_total integer not null,
    sl_total integer not null,
    primary key(train_no, doj));;

drop table if exists ac_seats;
create table ac_seats(
    seat_no integer not null,
    seat_type varchar(2) not null,
    primary key(seat_no)
);;

drop table if exists sl_seats;
create table sl_seats(
    seat_no integer not null,
    seat_type varchar(2) not null,
    primary key(seat_no)
);;

drop table if exists tickets_data;  
create table tickets_data( 
    pnr varchar(200) not null, 
    passenger_name text not null,  
    coach_number integer not null, 
    berth_number integer not null, 
    berth_type varchar(2) not null, 
    train_number integer not null, 
    doj varchar(10) not null, 
    primary key (pnr, coach_number, berth_number, berth_type));;

insert into ac_seats values(1,'LB');
insert into ac_seats values(2,'LB');
insert into ac_seats values(7,'LB');
insert into ac_seats values(8,'LB');
insert into ac_seats values(13,'LB');
insert into ac_seats values(14,'LB');
insert into ac_seats values(3,'UB');
insert into ac_seats values(4,'UB');
insert into ac_seats values(9,'UB');
insert into ac_seats values(10,'UB');
insert into ac_seats values(15,'UB');
insert into ac_seats values(16,'UB');
insert into ac_seats values(5,'SL');
insert into ac_seats values(11,'SL');
insert into ac_seats values(17,'SL');
insert into ac_seats values(6,'SU');
insert into ac_seats values(12,'SU');
insert into ac_seats values(18,'SU');

insert into sl_seats values(1,'LB');
insert into sl_seats values(4,'LB');
insert into sl_seats values(9,'LB');
insert into sl_seats values(12,'LB');
insert into sl_seats values(17,'LB');
insert into sl_seats values(20,'LB');
insert into sl_seats values(2,'MB');
insert into sl_seats values(5,'MB');
insert into sl_seats values(10,'MB');
insert into sl_seats values(13,'MB');
insert into sl_seats values(18,'MB');
insert into sl_seats values(21,'MB');
insert into sl_seats values(3,'UB');
insert into sl_seats values(6,'UB');
insert into sl_seats values(11,'UB');
insert into sl_seats values(14,'UB');
insert into sl_seats values(19,'UB');
insert into sl_seats values(22,'UB');
insert into sl_seats values(7,'SL');
insert into sl_seats values(15,'SL');
insert into sl_seats values(23,'SL');
insert into sl_seats values(8,'SU');
insert into sl_seats values(16,'SU');
insert into sl_seats values(24,'SU');

