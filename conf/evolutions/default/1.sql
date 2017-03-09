# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table User (
  id                        bigint auto_increment not null,
  pleaseowrk                 bigint,
  constraint pk_page_retrieval primary key (testID))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table testEvolution;

SET FOREIGN_KEY_CHECKS=1;

