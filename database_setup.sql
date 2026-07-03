-- 1. Create and select the database
CREATE DATABASE IF NOT EXISTS rental_db;
USE rental_db;

-- 2. Create the User table
CREATE TABLE User (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 3. Create the Property table
CREATE TABLE Property (
    property_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT,
    title VARCHAR(150) NOT NULL,
    location VARCHAR(255) NOT NULL,
    rent_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES User(user_id)
);

-- 4. Create the Booking table
CREATE TABLE Booking (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT,
    tenant_id BIGINT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    booking_status VARCHAR(20) NOT NULL,
    FOREIGN KEY (property_id) REFERENCES Property(property_id),
    FOREIGN KEY (tenant_id) REFERENCES User(user_id)
);

-- 5. Create the Review table
CREATE TABLE Review (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT,
    tenant_id BIGINT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    FOREIGN KEY (property_id) REFERENCES Property(property_id),
    FOREIGN KEY (tenant_id) REFERENCES User(user_id)
);

-- 6. Create the Payment_Transaction table
CREATE TABLE Payment_Transaction (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT,
    tenant_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    transaction_ref VARCHAR(100) UNIQUE NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES Booking(booking_id),
    FOREIGN KEY (tenant_id) REFERENCES User(user_id)
);

USE rental_db;
ALTER TABLE Property ADD COLUMN is_active BOOLEAN DEFAULT TRUE;