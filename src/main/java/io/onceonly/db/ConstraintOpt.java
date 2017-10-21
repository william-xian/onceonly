package io.onceonly.db;

/* TODO
ALTER TABLE Persons	ADD CONSTRAINT pk_PersonID PRIMARY KEY (Id_P,LastName)
ALTER TABLE Orders  ADD CONSTRAINT fk_PerOrders FOREIGN KEY (Id_P) REFERENCES Persons(Id_P);
ALTER TABLE Persons ADD CONSTRAINT uc_PersonID UNIQUE (Id_P,LastName)
CREATE INDEX index_name ON table_name (column_name)
CREATE INDEX name ON table USING HASH (column);
CREATE UNIQUE INDEX name ON table (column [, ...]);
ALTER TABLE Persons DROP CONSTRAINT pk_PersonID
ALTER TABLE Persons DROP CONSTRAINT uc_PersonID
ALTER TABLE table_name DROP INDEX index_name
*/
public class ConstraintOpt {
	int order;
	String opt;
	ConstraintMeta cm;
	String toSql() {
		return null;
	}
}