DROP TABLE IF EXISTS settle ;
CREATE TABLE settle (
    od_no varchar(30),
    seq int,
    amount decimal(12, 5),
    PRIMARY KEY (od_no, seq)
);
