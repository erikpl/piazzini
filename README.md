# Piazzini
Database project for TDT4145 Data Modelling, Databases and Database Management Systems


## Database setup
1.	Choose whether you wish to create schema or use “create database” method.
2.	If you choose “create database” uncomment create database and use database. If you wish to alter entries in the database, simply uncomment drop database for every subsequent use.
3.	If you wish to use schemas find them in MySQL navigator. Right click on the empty field under the default schemas already existing (usually only consists of the “sys” schema) and choose to create new schema.
4.	Call the new schema “piazzini”, press apply and finish. A new schema called “piazzini” should show up in the schema’s navigator. Right click the new schema and set as default schema.
5.	You can now run the database, set up the tables and populate them with the queries at the bottom of the database. Remember to leave the “drop database”, “create database” and “use database” commented if you are using a schema for the first time, then you can uncomment if you wish to drop the tables and rebuild them.
6.	The instructor user is already setup as erikpl@protonmail.com with the password “abc123”.


## Building and running the project
1. Build the JAR using the following Maven command:
```
mvn clean package
```

2. Run the JAR:
```
java -jar target/piazzini.jar
```

Alternatively, you can perform all the necessary steps to run the application using the following Maven command:
```
mvn clean install exec:java -Dexec.mainClass=main.java.RunApp
```

You may also need to add the JAR file located in ```lib/mysql-connector-java-8.0.16``` to the class path.


## Documentation

### DBController
The DBController works as a collection of logic and methods that is to be used by the CommandLineInterface class. The methods in DBController are set up in a way that corresponds to the use cases and a connector that enables the class to use the created database. The connection is initiated by the connect() method.
The first method used after connecting to the database is the userLogin()-method. This method retrieves user data from the database that we can use to compare with the input user data and verify if the user is a registered user of the database.
The second method newThreadAsStudent() is used to handle use case 2 of creating a new post. It first creates a new thread and subsequently retrieves that thread’s ID to use for future use. Afterwards it creates a post and inserts that post into the thread. The ID from that post is also retrieved for future use. Next we make the first post the main post of the thread and finish up by adding the post in a folder and giving the post a tag.
The third method replyToThreadAsInstructor() is used to create a reply specifically made by an instructor in a thread. It creates a post in the previous thread created and uses the reply’s post_ID to mark it as an instructor’s answer in the thread.
The fourth method searchForPostByKeyword() is used to search for a specific keyword in posts. This is done by scanning the database for all posts that have the keyword in any position. If this keyword is found it prints this post’s post ID out to the user. 
The fifth method getUserStatisticsAsInstructor() is used to fetch user statistics with regards to use case 5. This method queries the database to extract the requested data in the requested format. Afterwards it prints the data out for the user to view. 


### CommandLineInterface
The CommandLineInterface is a class that handles the DBController methods and is the class that the user will interact with when choosing use cases. All variables are hard-coded for this code so the user simply needs to select use cases.
Firstly, the user must select one of the use cases or quit the program. This is handled by the user simply pressing the buttons 1-5 or 0 to quit. This is also checked by having a method that handles invalid use cases (if the user presses something else or if the code breaks) in the method handleInvalidUseCases. All successes, failures and errors in the code is recorded by the integers 1, 0 and -1 where 0 = OK, 1 = exit and -1 = invalid. 
The next method handles the valid use cases. These simply take in the number that was pressed at the start and execute the subsequent use case handlers depending on which number was pressed.
For all the previously mentioned methods in the DBController class there is a corresponding handle method in this class. These are all started by checking if the user is a student or an instructor to handle the different use case specifications. 
The first method handles the checks of database email + password to the input email + password.
The second method handles the making of a post as described in the newThreadAsStudent method. 
The third method handles creating an instructor reply to the post created.
The fourth method handles searching the database for a post that has the specified keyword.
The fifth method handles the final use case and retrieves the user statistics and must be viewed as an instructor. 


### RunApp
The RunApp class works as a “main class” and simply executes the application and the CommandLineInterface such that the user can choose which use case they wish to see. Running RunApp corresponds to running the application as a whole.


## Use cases

### Case 1
“A student logs into the system, i.e., check username and password (you do not need to encrypt/decrypt passwords). This should have e-mail and password as input, and these should match this info in the database.”
This use case is handled by the userLogin()- and handleLogin()-methods. The user selects this use case and checks if the input of their email and password is the same as some user in the database. If it is then the user receives a message that says they are logged in.

### Case 2
“A student makes a post belonging to the folder “Exam” and tagged with “Question”. Input to the use case should be a post and the texts “Exam” and “Question”.”
The second use case is handled by newThreadAsStudent()- and handleMakePost()-methods. The user selects the user case, and the hard-coded constants inputs the data into the database in which case the new thread is created, and the database now has the new entry.

### Case 3
“An instructor replies to a post belonging to the folder “Exam”. The input to this is the id of the post replied to. This could be the post created in use case 2.”
The third use case is handled by the replyToThreadAsInstructor()- and handleInstructorReply()- methods. The user is first checked upon selection (as they are for all the handle methods) if they are an instructor. If they are an instructor, the program inserts an instructor reply into the post created and is marked as an instructor reply.

### Case 4
“A student searches for posts with a specific keyword “WAL”. The return value of this should be a list of ids of posts matching the keyword.”
The fourth use case is handled by searchForPostByKeyword()- and handlePostSearch()-methods. The input is also hard-coded, so the case automatically searches for a post with the specified keyword and returns a list of entries in the database with the post IDs of matching strings. 

### Case 5
“An instructor views statistic for users and how many posts they have read and how many they have created. These should be sorted on highest read posting numbers. The output is “username, number of posts read, number of posts created”. You don’t need to order by posts created, but the number should be displayed. The result should also include users which have not read or created posts.”
This final case is handled by the getUserStatisticsAsInstructor()- and handleViewStatistics()-methods. When the methods are called the database executes the query and returns the list with requested data. This list is then printed for the user to see where they can see the email, number of views and number of created posts. 
