package main.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBController {
    private Connection connection;
    private String currentUserEmail;
    private int studentThreadId;
    private int studentPostId;
    private int instructorPostId;
    private boolean studentThreadIsCreated = false;

    public boolean isStudentThreadIsCreated() {
        return studentThreadIsCreated;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }
    // Instructor privileges are managed in the CLI class

    // Empty constructor
    public DBController() {
        connect();
    }

    // Method for connecting to the MySQL server
    public void connect() {
        try {
            // Properties object to store DB user credentials
            Properties credentials = new Properties();
            credentials.put("user", "root");
            credentials.put("password", "12345678");
            this.connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/piazzini?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false", credentials);
            this.connection.setAutoCommit(false);
        }
        // If unable to connect to the MySQL server
        catch (Exception e) {
            throw new RuntimeException("Unable to connect", e);
        }
    }

    // Return -1 for exception
    // Return 0 for successful login
    // Return 1 for invalid credentials
    public int userLogin(String email, String password) {
        try {
           PreparedStatement query = this.connection.prepareStatement(String.format(
                    "SELECT email FROM piazza_user " +
                            "WHERE piazza_user.password=\"%s\" " +
                            "AND piazza_user.email=\"%s\";", password, email));

            ResultSet queryResult = query.executeQuery();

            if (queryResult.next()) {
                this.currentUserEmail = queryResult.getString("email");
                System.out.println("You are now logged in as " + this.currentUserEmail);
                return 0;
            } else {
                System.out.println("The username and/or password is wrong. Please try again.");
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Return -1 for exception
    // Return 0 for successful thread creation
    // TODO: fix ugly code lel
    public int newThreadAsStudent(String examFolderId, String postDescription, String threadTitle, String courseId, String tagId) {
        try {
            // 1. create Thread with threadTitle and commit changes.
            PreparedStatement createThreadQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO thread (thread_id, title, course_id) " +
                            "VALUES(DEFAULT, \"%s\", %s);", threadTitle, courseId
            ));
            createThreadQuery.executeUpdate();
            createThreadQuery.close();
            this.connection.commit();
            System.out.println("Created thread...");

            // 2. retrieve threadId of the created thread (set automatically).
            PreparedStatement retrieveThreadIdQuery = this.connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet retrieveThreadIdResult = retrieveThreadIdQuery.executeQuery();

            while (retrieveThreadIdResult.next()) {
                this.studentThreadId = retrieveThreadIdResult.getInt(1);
            }
            retrieveThreadIdQuery.close();

            // 3. create Post using threadId and postDescription. Last_edited auto-update.
            PreparedStatement makePostQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post (post_id, post_description, is_anonymous, last_edited, email, thread_id, self_post_id) " +
                            "VALUES(DEFAULT, \"%s\", TRUE, NOW(), \"%s\", %s, NULL);", postDescription, currentUserEmail, String.valueOf(studentThreadId)
            ));
            makePostQuery.executeUpdate();
            makePostQuery.close();
            this.connection.commit();
            System.out.println("Created main post for thread...");

            // 4. retrieve post_id of created Post.
            PreparedStatement retrievePostIdQuery = this.connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet retrievePostIdResult = retrievePostIdQuery.executeQuery();

            while (retrievePostIdResult.next()) {
                this.studentPostId = retrievePostIdResult.getInt(1);
            }
            retrievePostIdQuery.close();

            // 5. update the post_id of the created Thread to make the created post the main post.
            PreparedStatement setMainPostQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO main_post (thread_id, post_id) " +
                            "VALUES(%s, %s);", String.valueOf(this.studentThreadId), String.valueOf(this.studentPostId)
            ));
            setMainPostQuery.executeUpdate();
            setMainPostQuery.close();
            this.connection.commit();
            System.out.println("Connected post to thread...");

            // 6. create PostInFolder using the folderId and postId.
            PreparedStatement setPostFolderQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post_in_folder (post_id, folder_id) "
                            + "VALUES(%s, %s);", this.studentPostId, examFolderId
            ));
            setPostFolderQuery.executeUpdate();
            setPostFolderQuery.close();
            this.connection.commit();
            System.out.println("Put post in Exam folder...");

            this.studentThreadIsCreated = true;

            // 7. create PostHasTag using the tagId and postId
            PreparedStatement setPostTagQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post_has_tag (post_id, tag_id) " +
                            "VALUES (%s, %s);", this.studentPostId, tagId
            ));
            setPostTagQuery.executeUpdate();
            setMainPostQuery.close();
            this.connection.commit();
            System.out.println("Assigned \"Question\" tag to post...");

            System.out.println("Done!");
            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Return -1 for exception
    // Return 0 for successful thread reply
    public int replyToThreadAsInstructor(String postDescription) {
        try {
            // 1. create Post using postDescription and the postId + threadId stored as a class variable.
            // CLI class makes sure currentUserEmail corresponds to the instructor email
            PreparedStatement createInstructorReplyQuery = this.connection.prepareStatement(String.format(
                    "INSERT INTO post (post_id, post_description, is_anonymous, last_edited, email, thread_id, self_post_id) " +
                            "VALUES(DEFAULT, \"%s\", TRUE, NOW(), \"%s\", %s, %s);", postDescription, this.currentUserEmail, this.studentThreadId, this.studentPostId
            ));
            createInstructorReplyQuery.executeUpdate();
            createInstructorReplyQuery.close();
            System.out.println("Creating instructor reply...");
            this.connection.commit();

            // 2. retrieve the post_id of the created instructor reply
            PreparedStatement retrievePostIdQuery = this.connection.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet retrievePostIdResult = retrievePostIdQuery.executeQuery();

            while (retrievePostIdResult.next()) {
                this.instructorPostId = retrievePostIdResult.getInt(1);
            }
            retrievePostIdQuery.close();

            // 3. set created Post as Instructor's answer (Thread attribute).
            PreparedStatement setPostAsInstructorAnswerQuery = this.connection.prepareStatement(String.format(
                    "UPDATE thread SET instructor_answer_id = %s WHERE thread_id = %s;", this.instructorPostId, this.studentThreadId
            ));
            setPostAsInstructorAnswerQuery.executeUpdate();
            setPostAsInstructorAnswerQuery.close();
            this.connection.commit();
            System.out.println("Assigns post as the thread's Instructor's answer...");
            System.out.println("Done!");
            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    // Return -1 for exception
    // Return 0 for successful search
    public int searchForPostByKeyword(String keywordPattern) {
        try {
            // 1. Retrieve all matching posts using the LIKE operator
            PreparedStatement retrieveMatchingPostsQuery = this.connection.prepareStatement(String.format(
                    "SELECT post_id FROM post " +
                            "WHERE post.post_description LIKE \"%s\";", keywordPattern
            ));
            ResultSet retrieveMatchingPostsResult = retrieveMatchingPostsQuery.executeQuery();

            // 2. Print out all matching results
            int counter = 1;
            while (retrieveMatchingPostsResult.next()) {
                // 3. Format: Search result number x: post_id
                System.out.println("\nSearch result number " + counter + ":\t" + "post id=" + retrieveMatchingPostsResult.getInt("post_id"));
                counter++;
            }

            retrieveMatchingPostsQuery.close();

            System.out.println("Done!");

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Return 0 if successful
    // Return -1 if an exception occurred
    public int getUserStatisticsAsInstructor() {
        try {
            // 1. Retrieve user statistics from database
            PreparedStatement retrieveStatisticsQuery = this.connection.prepareStatement(
                    "SELECT stat1.email,stat1.viewCount,stat2.createCount FROM " +
                            "(SELECT piazza_user.email, COUNT(post_read.post_ID) AS viewCount " +
                            "FROM piazza_user LEFT JOIN post_read ON piazza_user.email = post_read.email " +
                            "GROUP BY piazza_user.email) AS stat1 LEFT JOIN  " +
                            "(SELECT piazza_user.email, COUNT(post_create.post_id) AS createCount " +
                            "FROM piazza_user LEFT JOIN post_create ON piazza_user.email = post_create.email " +
                            "GROUP BY piazza_user.email) AS stat2 ON stat1.email = stat2.email " +
                            "ORDER BY viewCount DESC;"
            );

            ResultSet retrieveStatisticsResult = retrieveStatisticsQuery.executeQuery();

            System.out.println("User statistics:");
            // 2. Print out each user's email, view count and create count
            while (retrieveStatisticsResult.next()) {
                String email = retrieveStatisticsResult.getString("email");
                int viewCount = retrieveStatisticsResult.getInt("viewCount");
                int createCount = retrieveStatisticsResult.getInt("createCount");
                System.out.println("\nUser: " + email);
                System.out.println("Posts viewed: " + viewCount);
                System.out.println("Posts created: " + createCount + "\n");
            }

            retrieveStatisticsQuery.close();

            System.out.println("Done!");

            return 0;
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String... args) {
        DBController dbController = new DBController();
        // 1: user login
        dbController.userLogin("audunrb@icloud.com", "passord");
        // 2: student question
        dbController.newThreadAsStudent("1", "Hits for kids vol 32", "follow me on sc pls", "1", "1");
        // 3: instructor answer
        dbController.userLogin("erikpl@protonmail.com", "abc123");
        dbController.replyToThreadAsInstructor("No MVDs!", "erikpl@protonmail.com");
        // 4: student search
        dbController.userLogin("audunrb@icloud.com", "passord");
        dbController.searchForPostByKeyword("%WAL%");
        // 5: user stats as professor
        dbController.getUserStatisticsAsInstructor();
    }
}
    /*
    private boolean isInstructor(String email) {
        try {
            PreparedStatement isInstructorQuery = this.connection.prepareStatement(String.format(
                    "SELECT email (email, course_id) " +
                            "FROM is_instructor " +
                            "WHERE is_instructor.email=%s;", email
            ));
            ResultSet isInstructorResult = isInstructorQuery.executeQuery();

            if (isInstructorResult.next()) {
                System.out.println(email + " is an instructor.");
                return true;
            }

            else {
                System.out.println(email + " is not an instructor.");
                return false;
            }
        }
        catch (Exception e) {
            System.out.println("Could not verify instructor status of " + email + ".");
        }
     */


