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

ALTER TABLE assets
    ADD CONSTRAINT assets_pk PRIMARY KEY (id);

ALTER TABLE attributes
    ADD CONSTRAINT attributes_pk PRIMARY KEY (id);

ALTER TABLE assets
    ADD CONSTRAINT assets_parentid_fk FOREIGN KEY (parentid) REFERENCES assets(id) ON DELETE CASCADE;
	
ALTER TABLE attributes
    ADD CONSTRAINT attributes_assetid_fk FOREIGN KEY (assetid) REFERENCES assets(id) ON DELETE CASCADE;
	
CREATE UNIQUE INDEX assets_id_idx ON assets(id);
CREATE INDEX assets_parentid_idx ON assets(parentid);

CREATE UNIQUE INDEX attributes_id_idx ON attributes(id);
CREATE INDEX attributes_assetid_idx ON attributes(assetid);
