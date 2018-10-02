CREATE TABLE assets (
    id numeric(22,0) NOT NULL,
    name character varying(100) NOT NULL,
	parentid numeric(22,0)
);

CREATE TABLE attributes (
    id numeric(22,0) NOT NULL,
    assetid numeric(22,0) NOT NULL,
    value character varying(200)
);

ALTER TABLE ONLY assets
    ADD CONSTRAINT assets_pk PRIMARY KEY (id);

ALTER TABLE ONLY attributes
    ADD CONSTRAINT attributes_pk PRIMARY KEY (id);

ALTER TABLE ONLY assets
    ADD CONSTRAINT assets_parentid_fk FOREIGN KEY (parentid) REFERENCES assets(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;	
	
ALTER TABLE ONLY attributes
    ADD CONSTRAINT attributes_assetid_fk FOREIGN KEY (assetid) REFERENCES assets(id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED;	
	
CREATE UNIQUE INDEX assets_id_idx ON assets USING btree (id);
CREATE INDEX assets_parentid_idx ON assets USING btree (parentid);

CREATE UNIQUE INDEX attributes_id_idx ON attributes USING btree (id);
CREATE INDEX attributes_assetid_idx ON attributes USING btree (assetid);
