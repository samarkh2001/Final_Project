DROP SCHEMA IF EXISTS park_db;
CREATE SCHEMA park_db;
USE park_db;

CREATE TABLE city (
    city_name VARCHAR(50) PRIMARY KEY
);

CREATE TABLE park (
    park_id INT AUTO_INCREMENT PRIMARY KEY,
    park_name VARCHAR(100),
	city_name VARCHAR(50),
    x_coord DOUBLE,
    y_coord DOUBLE,
    row_cnt INT,
    col_cnt INT,
    price DOUBLE,
    UNIQUE (city_name, park_name),
    FOREIGN KEY (city_name) REFERENCES city(city_name)
);

CREATE TABLE slot (
    park_id INT,
    row_id INT,
    col_id INT,
    enter_date DATE,
    enter_hour INT,
    enter_mins INT,
    park_status BOOLEAN,
    PRIMARY KEY (park_id, row_id, col_id),
    FOREIGN KEY (park_id) REFERENCES park(park_id)
);

CREATE TABLE users (
    fname VARCHAR(50),
    lname VARCHAR(50),
    email VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50)
);

CREATE TABLE ticket(
	park_id INT,
    plateId VARCHAR(10),
    entry_date DATE,
    entryHr INT,
    entryMin INT,
    leaveHr INT,
    leaveMin INT,
    paid DOUBLE,
	FOREIGN KEY (park_id) REFERENCES park(park_id)

);

CREATE TABLE full_park(
	park_id INT,
	date DATE,
    hr INT,
    cnt INT,
	FOREIGN KEY (park_id) REFERENCES park(park_id),
    PRIMARY KEY (park_id, date, hr)
);

INSERT INTO city (city_name)
VALUES 
    ('Haifa'),
    ('Karmiel'),
    ('Tel-Aviv');

INSERT INTO park (park_name, city_name, x_coord, y_coord, row_cnt, col_cnt, price)
VALUES
    ('Grand Mall', 'Haifa',1, 2, 4, 7, 0.25),
    ('Rambam Hospital', 'Haifa', 3, 4, 3, 5, 0.21),
	('Braude College of Engineering', 'Karmiel',1, 2, 2, 4, 0.28),
    ('BIG Mall', 'Karmiel', 3, 4, 1, 6, 0.29),
	('City Center', 'Tel-Aviv',1, 2, 4, 2, 0.20),
    ('Tel-Aviv University', 'Tel-Aviv', 3, 4, 3, 7, 0.19),
    ('Azrieli Mall', 'Tel-Aviv', 3, 4, 1, 2, 0.8);
    
    INSERT INTO users (fname, lname, email, password) 
		VALUES
			("Samar", "Khalil", "s@s.s", "123"),
            ("Abode", "Aburomi", "a@a.a", "123"),
            ("Qamar", "Awad", "q@q.q", "12345");
            
	INSERT INTO full_park(park_id, date, hr, cnt)
	VALUES
		(1, '2025-01-29', 12, 3),
		(1, '2025-01-27', 17, 3),
        (1, '2025-01-29', 8, 8),
        
        (2, '2025-01-28', 12, 3),
		(2, '2025-01-29', 8, 8),
        (3, '2025-01-29', 12, 3);
    

