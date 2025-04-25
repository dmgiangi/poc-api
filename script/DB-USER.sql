USE [sqldb-main];

CREATE SCHEMA [fido-poc];
GO

BEGIN TRANSACTION;

CREATE USER [fido2-poc] FROM EXTERNAL PROVIDER;
GO

GRANT SELECT ON SCHEMA ::[fido-poc] TO [fido2-poc];
GRANT INSERT ON SCHEMA ::[fido-poc] TO [fido2-poc];
GRANT UPDATE ON SCHEMA ::[fido-poc] TO [fido2-poc];
GRANT DELETE ON SCHEMA::[fido-poc] TO [fido2-poc];
GRANT ALTER ON SCHEMA::[fido-poc] TO [fido2-poc];
GRANT CONTROL ON SCHEMA::[fido-poc] TO [fido2-poc];
GO

COMMIT