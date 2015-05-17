create table services (
    id BIGINT not null primary key,
    urn varchar(100) not null,
    endpoint_address varchar(100) not null
);

insert into services (id, urn, endpoint_address) values (1, 'http://servicios.msp.gub.uy/servicio', 'http://localhost:8080/ws');
insert into services (id, urn, endpoint_address) values (2, 'http://servicios.msp.gub.uy/servicio2', 'saaa');