ALTER SYSTEM SET max_connections = 350;  -- Adjust this value as needed


create table dependentTestEntity (
                                     id bigserial not null,
                                     name varchar(255),
                                     primary key (id)
)



create table employee (
                                            id bigserial not null,
                                            name varchar(255),
                                            singleDependentTestEntity_id bigint,
                                            primary key (id)
)


create table relationshipRootTestEntity_dependentTestEntity (
                                                                RelationshipRootTestEntity_id bigint not null,
                                                                dependentTestEntityList_id bigint not null
)


create table item (
                                           id bigserial not null,
                                           name varchar(255),
                                           primary key (id)
)


create table singleTestEntity (
                                  id bigserial not null,
                                  name varchar(255),
                                  primary key (id)
)

alter table if exists relationshipRootTestEntity_dependentTestEntity
    add constraint UK_7ja7meqt3ufwu9q0lup1mhajw unique (dependentTestEntityList_id)


alter table if exists employee
    add constraint FK4nv2e5ewb18clnvcxr3fwohey
    foreign key (singleDependentTestEntity_id)
    references item


alter table if exists relationshipRootTestEntity_dependentTestEntity
    add constraint FKgyufpeiqjleshpyr7fnowatpq
    foreign key (dependentTestEntityList_id)
    references dependentTestEntity


alter table if exists relationshipRootTestEntity_dependentTestEntity
    add constraint FKccl9i2jjq596jlui41viojmea
    foreign key (RelationshipRootTestEntity_id)
    references employee

