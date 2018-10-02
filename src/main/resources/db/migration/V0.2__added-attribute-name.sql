DROP INDEX attributes_id_idx;
ALTER TABLE attributes DROP CONSTRAINT attributes_pk CASCADE;

ALTER TABLE attributes
    DROP COLUMN id CASCADE;

ALTER TABLE attributes ADD COLUMN name character varying(100) NOT NULL;

ALTER TABLE ONLY attributes
    ADD CONSTRAINT attributes_pk PRIMARY KEY (name, assetid);

CREATE UNIQUE INDEX attributes_pk_idx ON attributes USING btree (name, assetid);