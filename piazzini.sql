create database piazzadatabase;
use piazzadatabase;

drop table gives_good_comment;
drop table post_in_folder;
drop table post_has_tag;
drop table is_instructor;
drop table tag_in_course;
drop table main_post;
drop table post_read;

drop table post;
drop table thread;
drop table folder;
drop table tag;
drop table course;
drop table piazza_user;



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
  course_id	INT UNSIGNED NOT NULL,
  post_id 	INT UNSIGNED,
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
  self_post_id		INT UNSIGNED,
  PRIMARY KEY (post_id),
  CONSTRAINT author FOREIGN KEY (email) 	
  REFERENCES piazza_user(email)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT post_ref_post FOREIGN KEY (self_post_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  CONSTRAINT thread_post FOREIGN KEY (thread_id) 	
  REFERENCES thread(thread_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

ALTER TABLE thread
  ADD CONSTRAINT student_answer FOREIGN KEY (post_id)
  REFERENCES post(post_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    
  ADD CONSTRAINT instructor_answer FOREIGN KEY (post_id)
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



INSERT INTO piazza_user VALUES("audunrb@icloud.com", "audun", "bøe", "passord");
INSERT INTO piazza_user VALUES("erikpl@protonmail.com", "erik", "løvaas", "abc123");
INSERT INTO piazza_user VALUES("amart@ladmail.com", "amar", "taso", "test123");
INSERT INTO tag VALUES(DEFAULT, "Question");
INSERT INTO course VALUES(DEFAULT, "database og datamodellering", 'spring', true);
INSERT INTO folder VALUES(DEFAULT, "folder3", 1);
INSERT INTO folder VALUES(DEFAULT, "folder2", 1);
INSERT INTO post VALUES(DEFAULT, "beskrivelse", TRUE, NOW(), 'audunrb@icloud.com', NULL, NULL);
INSERT INTO thread VALUES(DEFAULT, "tittel", 1 , 1);
INSERT INTO post VALUES(DEFAULT, 'boyo', TRUE, NOW(), "erikpl@protonmail.com", 1, NULL);
INSERT INTO post VALUES(DEFAULT, 'boyos', TRUE, NOW(), "erikpl@protonmail.com", 1, 3);
INSERT INTO post VALUES(DEFAULT, 'boyoso', TRUE, NOW(), "amart@ladmail.com", 1, NULL);
INSERT INTO post VALUES(DEFAULT, 'boyosos', TRUE, NOW(), "amart@ladmail.com", NULL, 5);
INSERT INTO thread VALUES(DEFAULT, "snap", 1, 4);
INSERT INTO thread VALUES(DEFAULT, "stop it", 1, 2);
INSERT INTO thread VALUES(DEFAULT, "get some help", 1, 3);
INSERT INTO gives_good_comment VALUES('audunrb@icloud.com', 2);
INSERT INTO post_in_folder VALUES(2, 1);
INSERT INTO post_has_tag VALUES(3, 1);
INSERT INTO is_instructor VALUES("erikpl@protonmail.com", 1);
INSERT INTO tag_in_course VALUES(1, 1);
INSERT INTO main_post VALUES(2, 2);
INSERT INTO post_read VALUES("amart@ladmail.com", 4);
