create table if not exists invoices
(
    id          bigserial constraint invoices_pk primary key,
    payment_id  varchar not null,
    customer_id bigint  not null,
    status      varchar not null
);

create table if not exists products
(
    id         bigserial constraint products_pk primary key,
    unique_id  varchar not null,
    name       varchar not null,
    price      real    not null,
    count      integer not null,
    invoice_id bigint constraint products_invoices__fk references invoices
);

