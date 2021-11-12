CREATE TABLE Caregivers (
    Username varchar(255),
    Email varchar(255),
    Salt BINARY(16),
    Hash BINARY(16),
    PRIMARY KEY (Username)
);