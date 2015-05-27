create table events (
    id BIGINT not null primary key identity,
    datetime TIMESTAMP not null,
    application varchar(50) not null,
    username varchar(20) not null,
    service varchar(50) not null,
    method varchar(50) not null,
    messageId varchar(200) not null
) ;

