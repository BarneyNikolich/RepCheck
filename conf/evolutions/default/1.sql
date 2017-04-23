# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table User (
  id                        bigint auto_increment not null,
  pleaseowrk                 bigint,
  constraint pk_page_retrieval primary key (testID))
;

CREATE TABLE `repcheck`.`cheese` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NULL,
  `password` VARCHAR(45) NOT NULL,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  PRIMARY KEY (`username`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC));





# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table testEvolution;

SET FOREIGN_KEY_CHECKS=1;

