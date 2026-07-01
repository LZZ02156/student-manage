-- init.sql: create tables and sample data
CREATE DATABASE IF NOT EXISTS rollcall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE rollcall;

CREATE TABLE IF NOT EXISTS student (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    student_name VARCHAR(50) NOT NULL,
    class_name VARCHAR(50) NOT NULL,
    course_name VARCHAR(50) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS roll_call_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    class_name VARCHAR(50) NOT NULL,
    course_name VARCHAR(50) NOT NULL,
    face_image_url VARCHAR(255),
    location VARCHAR(100),
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    is_valid TINYINT DEFAULT 1,
    INDEX idx_class_course (class_name, course_name),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student(student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(50) NOT NULL,
    class_name VARCHAR(50) NOT NULL,
    teacher_name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- sample data
INSERT INTO student (student_id, student_name, class_name, course_name) VALUES
('2023001','张三','20软工1','操作系统'),
('2023002','李四','20软工1','操作系统'),
('2023003','王五','20软工1','操作系统');

INSERT INTO course (course_name, class_name, teacher_name) VALUES
('操作系统','20软工1','陈老师');
