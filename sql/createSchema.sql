AUTOCOMMIT OFF;

CREATE TABLE fp (
    id VARCHAR(32) NOT NULL,
    time BIGINT NOT NULL,
    ip_address VARCHAR(128),
    campaign_id VARCHAR(32),
    template_id VARCHAR(32),
    message_id VARCHAR(32),
    destination_url VARCHAR(128),
    browser_fp CLOB(1 M)
);

ALTER TABLE fp
   ADD CONSTRAINT fp_pk Primary Key (id);

COMMIT;
