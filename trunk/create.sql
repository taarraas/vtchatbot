drop table if exists selftrainingmessages;

create table selftrainingmessages (
	id INT not null primary key auto_increment,
	message VARCHAR(256) not null default "",
	key (message)
) CHARACTER SET utf8;

drop table if exists selftrainingconnections;

create table selftrainingconnections (
	idprevremark INT not null,
	idcurremark INT not null,
	idanswer INT not null,
	key (idprevremark),
	key (idcurremark)
) CHARACTER SET utf8;

drop table if exists preprogrammedanswers;

create table preprogrammedanswers (
	wordtype INT not null,
	message VARCHAR(256) not null default "",
	key (wordtype)
) CHARACTER SET utf8;

drop table if exists preprogrammedwords;

create table preprogrammedwords (
	word VARCHAR(20) not null unique,
	comparetype INT not null,
	casesensetive TINYINT(1) default 0,
	type INT not null
) CHARACTER SET utf8;

drop table if exists preprogrammedtimeout;

create table preprogrammedtimeout (
	minimumremarksbefore INT not null key,
	message VARCHAR(256) not null default ""
) CHARACTER SET utf8;

drop table if exists revalentinfo;

create table revalentinfo (
	first INT not null,
	second INT not null,
	value DOUBLE not null
) CHARACTER SET utf8;

drop table if exists userinfo;

create table userinfo (
	identifier VARCHAR(128) NOT NULL KEY,
	sessionCount INT not null,
	asBotSessionCount INT not null	
) CHARACTER SET utf8;

insert into preprogrammedwords (word, comparetype, type) values
	("BEFORE_FIRST_REMARK", 0, 1),
	("бот", 0, 2),
	("бота", 0, 2),
	("?", 1, 3),
	("что", 0, 3),
	("как", 0, 3),
	("зачем", 0, 3),
	("почему", 0, 3);

insert into preprogrammedanswers (wordtype, message) values
	(0, "да ты умница))"),
	(0, "жжош"),
	(0, "Как ты относишся к проблеме клонирования?"),
	(0, "ты меня любишь?"),
	(0, "ех, бридке буття..."),
	(0, "от скажи чесно, почему ты со мной розговариваеш?"),
	(0, "я в Киеве, давай встретимся?"),
	(1, "Приветик:)"),
	(1, "привет"),
	(1, "приветик)"),
	(2, "ну и что, что бот?))"),
	(2, "прям таки бот уже тебе не человек)"),
	(2, "боты жгут нипадецки"),
	(3, "незнаю"),
	(3, "сам знаешь"),
	(3, "будешь много знать -быстро постареешь))"),
	(3, "нискажу йа тибе ничего"),
	(3, "а зачем тебе это?");
insert into preprogrammedtimeout (minimumremarksbefore, message) values 
	(0,"почему ты молчишь"),
	(4,"ты где?"),
	(9,"не спать!");
