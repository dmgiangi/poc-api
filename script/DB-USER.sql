USE [sqldb-main];

CREATE SCHEMA [fido-poc];
GO

BEGIN TRANSACTION;

CREATE USER [fido2-poc-id-be23] FROM EXTERNAL PROVIDER;
GO

GRANT SELECT ON SCHEMA ::[fido-poc] TO [fido2-poc-id-be23];
GRANT INSERT ON SCHEMA ::[fido-poc] TO [fido2-poc-id-be23];
GRANT UPDATE ON SCHEMA ::[fido-poc] TO [fido2-poc-id-be23];
GRANT DELETE ON SCHEMA::[fido-poc] TO [fido2-poc-id-be23];
GRANT ALTER ON SCHEMA::[fido-poc] TO [fido2-poc-id-be23];
GRANT CONTROL ON SCHEMA::[fido-poc] TO [fido2-poc-id-be23];
GO

COMMIT