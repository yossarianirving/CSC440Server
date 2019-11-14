CREATE TABLE course(
	id INT PRIMARY KEY,
	title CHAR(7) NOT NULL,
	requirement_satisfaction CHAR(50) NOT NULL,
	credits DOUBLE NOT NULL,
	semester_taken CHAR(12) NOT NULL,
	year_taken INT(4) NOT NULL,
	final_grade CHAR(1),
	UNIQUE (title, semester_taken, year_taken)
);
CREATE TABLE assignment(
	id INT PRIMARY KEY,
	title CHAR(50),
	weight DOUBLE NOT NULL,
	grade DOUBLE NOT NULL,
	course_id INT NOT NULL,
	CONSTRAINT course_id_constraint FOREIGN KEY (course_id) REFERENCES course(id),
	UNIQUE (title, course_id)
);