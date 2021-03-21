# piazzini
Database project for TDT4145 Data Modelling, Databases and Database Management Systems

## User stories
The main purpose of this project is to teach data modelling, SQL and database management in practice, so the required functionality is kept to a minimum. This functionality is specified in the form of the following user stories:
1. A student logs into the system, i.e., check user name and password. No encryption.
2. A student makes a post belonging to the folder “Exam” and tagged with “Question”.
3. An instructor replies to a post belonging to the folder “Exam”. The input to this is the id of the post replied to. This could be the post created in use case 2.
4. A student searches for posts with a specific keyword “WAL”. The return value of this should be a list of ids of posts matching the keyword.
5. An instructor views statistics for users and how many post they have read and how many they have created. These should be sorted on highest read posting numbers. 
