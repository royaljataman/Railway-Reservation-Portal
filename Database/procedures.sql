drop procedure if exists add_train(integer,character varying,integer,integer );
create or replace procedure add_train(
        train_number integer,
        doj varchar(10),
        ac_max integer,
        sl_max integer)
        language plpgsql 
        as $$
        begin 
            insert into train_data values(train_number, doj, ac_max, sl_max);
            insert into seats_filled values(train_number, doj, 0 , 0 , 18*ac_max,24*sl_max);
            end;
        $$;;

drop procedure if exists booking(integer,text,text,integer,character varying,text);
create or replace procedure booking(
IN filled integer,
IN pnr text,
IN r_name text,
IN r_train_number integer,
IN r_doj varchar(10),
IN r_coach_choice text) 
language plpgsql 
    as $$ 
    declare 
        seat_no1 integer default(0);
        coach integer;
        berth_number integer;
        berth_type text; 
    begin 
        if r_coach_choice = 'AC' then 
            seat_no1 = filled+1;
            berth_number = MOD(seat_no1-1, 18)  +1;
            coach = (seat_no1 - 1)/18 + 1;
        
            select seat_type
            into berth_type
            from ac_seats
            where seat_no = berth_number;

        end if;
        
        if r_coach_choice = 'SL' then 
            seat_no1 = filled+1;
            berth_number = MOD(seat_no1-1, 24)+1;
            coach = (seat_no1 - 1)/24 + 1;
            select seat_type
                into berth_type
                from sl_seats
                where seat_no = berth_number;
        end if;
        insert into tickets_data values(pnr, r_name, coach, berth_number, berth_type, r_train_number, r_doj);
    end
$$;;

-- call add_train(6859, '2023-03-08', 3,3);
-- call add_train(5750, '2023-05-27', 3,3);
-- call add_train(6481, '2023-05-15', 3,3);