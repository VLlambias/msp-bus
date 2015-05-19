create database service_catalog;
connect service_catalog;

create table services (
    id BIGINT not null primary key,
    urn varchar(100) not null,
    endpoint_address varchar(100) not null
) engine = InnoDb;

insert into services (id, urn, endpoint_address) values (1, 'http://servicios.msp.gub.uy/servicio', 'http://localhost:8080/ws');
insert into services (id, urn, endpoint_address) values (2, 'http://servicios.msp.gub.uy/servicio', 'aaa');
insert into services (id, urn, endpoint_address) values (3, 'http://servicios.msp.gub.uy/servicioHttp404Error', 'http://localhost:18080/noServiceHere');
insert into services (id, urn, endpoint_address) values (4, 'http://servicios.msp.gub.uy/servicioConnectionError', 'http://10.255.255.10:18080/connectionError');
insert into services (id, urn, endpoint_address) values (5, 'http://servicios.msp.gub.uy/servicioConnectionTimeoutError', 'http://localhost:8088/mockEchoServiceServiceSoapBinding');