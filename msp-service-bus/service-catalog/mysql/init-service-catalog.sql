create database service_catalog;
connect service_catalog;

create table services (
    id BIGINT not null primary key,
    urn varchar(100) not null,
    endpoint_address varchar(100) not null
) engine = InnoDb;

insert into services (id, urn, endpoint_address) values (1, 'http://servicios.msp.gub.uy/servicio', 'http://localhost:8080/ws');