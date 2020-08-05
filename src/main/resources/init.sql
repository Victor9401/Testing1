CREATE TABLE "bet"
(
    "id"         bigserial PRIMARY KEY not null,
    login        varchar,
    bet_num      int,
    bet_platform int,
    bet_date     bigint,
    calc_date    bigint,
    type_bet     int,
    pay_sum      decimal,
    bet_sum      decimal,
    sum_coef     decimal,
    cashout_is   int
);

CREATE TABLE "card"
(
    "id"       bigserial PRIMARY KEY not null,
    result     int,
    bet_id     bigint REFERENCES bet (id),
    coef       decimal,
    event_type int,
    match_id   int,
    sport      varchar,
    card_data_id bigint
)
