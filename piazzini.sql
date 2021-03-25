#Ignore drop, create and use if you are using schemas. Uncomment create and use for first time use of the database.
#Uncomment drop database after first use if you wish to reset the database and tables.
#drop database piazzini;
#create database piazzini;
#use piazzini;

CREATE TABLE piazza_user (
  email      		VARCHAR(50) NOT NULL,
  first_name 		TEXT NOT NULL,
  last_name  		TEXT NOT NULL,
  password   		TEXT NOT NULL,
  PRIMARY KEY (email)
);

CREATE TABLE course (
  course_id   		INT UNSIGNED AUTO_INCREMENT NOT NULL,
  course_name 		TEXT NOT NULL,
  course_term 		ENUM('spring', 'summer', 'autumn', 'winter') NOT NULL,
  can_be_anonymous 	BOOLEAN NOT NULL,
  PRIMARY KEY (course_id)  
);

CREATE TABLE folder (
  folder_id		INT UNSIGNED AUTO_INCREMENT NOT NULL,
  folder_name 		TEXT NOT NULL,
  course_id		INT UNSIGNED NOT NULL, 
  PRIMARY KEY (folder_id),
  CONSTRAINT course_folder FOREIGN KEY (course_id)
  REFERENCES course(course_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE tag (
  tag_id 	INT UNSIGNED AUTO_INCREMENT NOT NULL,
  tag_name 	TEXT NOT NULL,
  PRIMARY KEY (tag_id)
);

CREATE TABLE thread (
  thread_id     INT UNSIGNED AUTO_INCREMENT NOT NULL,
  title         TEXT NOT NULL,
  course_id		INT UNSIGNED NOT NULL,
  student_answer_id 	INT UNSIGNED,
  instructor_answer_id 	INT UNSIGNED,
  PRIMARY KEY (thread_id)
);

CREATE TABLE post (
  post_id     		INT UNSIGNED AUTO_INCREMENT NOT NULL,
  post_description 	TEXT NOT NULL,
  is_anonymous 		BOOLEAN NOT NULL,
  last_edited		TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
			ON UPDATE CURRENT_TIMESTAMP NOT NULL,
  email			VARCHAR(50) NOT NULL,
  thread_id		INT UNSIGNED,
  ref_post_id		INT UNSIGNED,
  PRIMARY KEY (post_id),
  CONSTRAINT author FOREIGN KEY (email) 	
  REFERENCES piazza_user(email)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT post_ref_post FOREIGN KEY (ref_post_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT thread_post FOREIGN KEY (thread_id) 	
  REFERENCES thread(thread_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

ALTER TABLE thread
  ADD CONSTRAINT student_answer FOREIGN KEY (student_answer_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  ADD CONSTRAINT instructor_answer FOREIGN KEY (instructor_answer_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  ADD CONSTRAINT course_thread FOREIGN KEY (course_id)
  REFERENCES course(course_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE;

CREATE TABLE gives_good_comment (
  email			VARCHAR(50) NOT NULL,
  post_id		INT UNSIGNED NOT NULL,
  CONSTRAINT user_id FOREIGN KEY (email)
  REFERENCES piazza_user(email)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT post_id FOREIGN KEY (post_id)
  REFERENCES post(post_id)    
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (email, post_id)
);

CREATE TABLE post_in_folder (
  post_id		INT UNSIGNED NOT NULL,
  folder_id		INT UNSIGNED NOT NULL,
  CONSTRAINT post_post_id FOREIGN KEY (post_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT folder_id FOREIGN KEY (folder_id)
  REFERENCES folder(folder_id)    
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (post_id, folder_id)
);

CREATE TABLE post_has_tag (
  post_id		INT UNSIGNED NOT NULL,
  tag_id		INT UNSIGNED NOT NULL,
  CONSTRAINT tag_id FOREIGN KEY (tag_id)
  REFERENCES tag(tag_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT post_tag_id FOREIGN KEY (post_id)
  REFERENCES post(post_id)    
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (tag_id, post_id)
);

CREATE TABLE is_instructor (
  email			VARCHAR(50) NOT NULL,
  course_id		INT UNSIGNED NOT NULL,
  CONSTRAINT instructor_user_id FOREIGN KEY (email)
  REFERENCES piazza_user(email)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT course_id FOREIGN KEY (course_id)
  REFERENCES course(course_id)    
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (email, course_id)
);

CREATE TABLE tag_in_course (
  tag_id		INT UNSIGNED NOT NULL,
  course_id		INT UNSIGNED NOT NULL,
  CONSTRAINT tag_course_id FOREIGN KEY (tag_id)
  REFERENCES tag(tag_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT course_tag_id FOREIGN KEY (course_id)
  REFERENCES course(course_id)    
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (tag_id, course_id)
);

CREATE TABLE main_post (
  thread_id		INT UNSIGNED NOT NULL,
  post_id		INT UNSIGNED NOT NULL,
  CONSTRAINT thread_id FOREIGN KEY (thread_id)
  REFERENCES thread(thread_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT main_post_id FOREIGN KEY (post_id)
  REFERENCES post(post_id)    
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (thread_id, post_id)
); 

CREATE TABLE post_read (
  email			VARCHAR(50) NOT NULL,
  post_id		INT UNSIGNED NOT NULL,
  CONSTRAINT reader_email_id FOREIGN KEY (email)
  REFERENCES piazza_user(email)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT reader_post_id FOREIGN KEY (post_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (email, post_id)
);

CREATE TABLE post_create (
  email			VARCHAR(50) NOT NULL,
  post_id 		INT UNSIGNED NOT NULL,
  CONSTRAINT poster_email FOREIGN KEY (email)
  REFERENCES piazza_user(email)
	ON DELETE CASCADE
    ON UPDATE CASCADE,

  CONSTRAINT poster_post_id FOREIGN KEY (post_id)
  REFERENCES post(post_id)
	ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (email, post_id)
);



INSERT INTO piazza_user VALUES("audunrb@icloud.com", "audun", "bøe", "passord");
INSERT INTO piazza_user VALUES("erikpl@protonmail.com", "erik", "løvaas", "abc123");
INSERT INTO piazza_user VALUES("amart@ladmail.com", "amar", "taso", "test123");
INSERT INTO tag VALUES(DEFAULT, "Question");
INSERT INTO course VALUES(DEFAULT, "Data Modelling, Databases and Database Management Systems", 'spring', true);
INSERT INTO is_instructor VALUES("erikpl@protonmail.com", 1);
INSERT INTO tag_in_course VALUES(1, 1);
INSERT INTO folder VALUES(DEFAULT, "Exam", 1);
INSERT INTO folder VALUES(DEFAULT, "Midterm", 1);
INSERT INTO thread VALUES(DEFAULT, "Title", 1 , NULL, NULL);
INSERT INTO post VALUES(DEFAULT, "Question", TRUE, NOW(), 'audunrb@icloud.com', 1, NULL);
INSERT INTO main_post VALUES(1, 1);
INSERT INTO post_in_folder VALUES(1, 1);
INSERT INTO post_has_tag VALUES(1, 1);
INSERT INTO post VALUES(DEFAULT, 'Instructor answer', TRUE, NOW(), "erikpl@protonmail.com", 1, 1);
UPDATE thread SET instructor_answer_id = 2 WHERE thread_id = 1;
INSERT INTO post VALUES(DEFAULT, 'Comment', TRUE, NOW(), "audunrb@icloud.com", 1, NULL);
INSERT INTO post VALUES(DEFAULT, 'CommentWALLE 2', TRUE, NOW(), "erikpl@protonmail.com", 1, NULL);
INSERT INTO post VALUES(DEFAULT, 'Student answer', TRUE, NOW(), "amart@ladmail.com", 1, 1);
UPDATE thread SET student_answer_id = 5 WHERE thread_id = 1;
INSERT INTO gives_good_comment VALUES('audunrb@icloud.com', 2);
INSERT INTO post_read VALUES("audunrb@icloud.com", 2);
INSERT INTO post_read VALUES("audunrb@icloud.com", 5);
INSERT INTO post_read VALUES("amart@ladmail.com", 1);
INSERT INTO post_read VALUES("amart@ladmail.com", 2);
INSERT INTO post_read VALUES("erikpl@protonmail.com", 1);
INSERT INTO post_read VALUES("erikpl@protonmail.com", 3);
INSERT INTO post_read VALUES("erikpl@protonmail.com", 5);
INSERT INTO post_create VALUES("amart@ladmail.com", 5);
INSERT INTO post_create VALUES("erikpl@protonmail.com", 2);
INSERT INTO post_create VALUES("erikpl@protonmail.com", 4);
INSERT INTO post_create VALUES("audunrb@icloud.com", 1);
INSERT INTO post_create VALUES("audunrb@icloud.com", 3);
