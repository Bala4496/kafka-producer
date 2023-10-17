create table agents
(
    id           UUID        not null primary key,
    manufacturer varchar(64) not null,
    os           varchar(64) not null
);

create table telemetries
(
    id             UUID         not null primary key,
    agent_id       UUID,
    active_service varchar(255) not null,
    quality_score  smallint     not null,
    created_at     timestamp    not null default now(),
    constraint fk_agent_id foreign key (agent_id) references agents (id) on delete cascade
);
