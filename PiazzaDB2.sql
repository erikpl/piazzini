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
  user_email		VARCHAR(50) NOT NULL,
  thread_id		INT UNSIGNED NOT NULL,
  self_post_id		INT UNSIGNED NOT NULL,
  PRIMARY KEY (post_id),
  CONSTRAINT author FOREIGN KEY (user_email) 	
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



insert into piazza_user values("audunrb@icloud.com", "audun", "bøe", "passord");
insert into tag values(DEFAULT, "tag");

insert into course values(DEFAULT, "database og datamodellering", 'spring', true);
insert into folder values(DEFAULT, "folder", LAST_INSERT_ID());
/*
insert into post values(DEFAULT, "beskrivelse", true, NOW(), LAST_INSERT_ID(), LAST_INSERT_ID(), LAST_INSERT_ID());
insert into thread values(DEFAULT, "tittel", LAST_INSERT_ID(), LAST_INSERT_ID());
*/


/*
Forslag 1:
sett instructoranswer + studentanswer inn i post og pek mot thread
Forslag 2: 
La post og thread id = nullable, insert først en post med nullverdier ->
lag en thread med denne posten -> lag en ny post i denne threaden. 
*/




