CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50)
);

CREATE TABLE rollcall_session (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200),
  creator VARCHAR(100),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  require_location BOOLEAN DEFAULT FALSE,
  location_lat DOUBLE,
  location_lng DOUBLE,
  location_radius_m INT,
  require_qr BOOLEAN DEFAULT FALSE,
  qr_code VARCHAR(200),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE attendance_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  username VARCHAR(100) NOT NULL,
  status VARCHAR(20),
  checkin_time TIMESTAMP,
  checkin_lat DOUBLE,
  checkin_lng DOUBLE,
  proof_url VARCHAR(500),
  note VARCHAR(500),
  FOREIGN KEY (session_id) REFERENCES rollcall_session(id)
);
