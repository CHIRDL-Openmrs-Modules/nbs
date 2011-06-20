SET FOREIGN_KEY_CHECKS=0;

truncate table atd_form_instance;
truncate table atd_patient_atd;
truncate table atd_patient_state;
truncate table atd_session;
truncate table nbs_error;
truncate table encounter;
truncate table hl7_in_queue;
truncate table obs;
truncate table patient;
truncate table patient_identifier;
delete from person where person_id not in (select user_id from users where system_id in ('admin') );
delete from person_address where person_id not in (select user_id from users where system_id in ('admin') or );
delete from person_attribute where person_id not in (select user_id from users where system_id in ('admin') );
delete from person_name where person_id  not in (select user_id from users where system_id in ('admin') );
truncate table sockethl7listener_patient_message;
delete from user_role where user_id  not in (select user_id from users where system_id in ('admin') );
delete from users where user_id not in (select user_id from users where system_id in ('admin') );


SET FOREIGN_KEY_CHECKS=1;