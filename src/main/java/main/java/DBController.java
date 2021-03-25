package main.java;

import java.sql.*;
import java.util.Properties;

public class DBController {
    private Connection connection;
    private String currentUserEmail;
    private int studentThreadId;
    private int studentPostId;
    private int instructorPostId;


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


    public void userLogin(String email, String password) throws SQLException {
        PreparedStatement authQuery = connection.prepareStatement(String.format(
                "SELECT email FROM piazza_user " +
                        "WHERE piazza_user.password=\"%s\" " +
                        "AND piazza_user.email=\"%s\";", password, email));

        ResultSet authResult = authQuery.executeQuery();

        if (authResult.next()) {
            currentUserEmail = authResult.getString("email");
            System.out.println("You are now logged in as " + currentUserEmail);
        }
    }


    public void newThreadAsStudent(String examFolderId, String postDescription, String threadTitle, String courseId, String tagId) throws SQLException {
        // 1. create Thread with threadTitle and commit changes.

        PreparedStatement createThreadQuery = connection.prepareStatement(String.format(
                "INSERT INTO thread (thread_id, title, course_id) " +
                        "VALUES(DEFAULT, \"%s\", %s);", threadTitle, courseId
        ));
        createThreadQuery.executeUpdate();
        createThreadQuery.close();
        connection.commit();
        System.out.println("Created thread...");

        // 2. retrieve threadId of the created thread (set automatically).

        PreparedStatement retrieveThreadIdQuery = connection.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet retrieveThreadIdResult = retrieveThreadIdQuery.executeQuery();

        while (retrieveThreadIdResult.next()) {
            studentThreadId = retrieveThreadIdResult.getInt(1);
            System.out.println("\nCreated thread with thread ID: " + retrieveThreadId.getInt("thread_id"));
        }
        retrieveThreadIdQuery.close();

        // 3. create Post using threadId and postDescription. Last_edited auto-update.

        PreparedStatement makePostQuery = connection.prepareStatement(String.format(
                "INSERT INTO post (post_id, post_description, is_anonymous, last_edited, email, thread_id, self_post_id) " +
                        "VALUES(DEFAULT, \"%s\", TRUE, NOW(), \"%s\", %s, NULL);", postDescription, currentUserEmail, studentThreadId
        ));
        makePostQuery.executeUpdate();
        makePostQuery.close();
        connection.commit();
        System.out.println("Created main post for thread...");

        // 4. retrieve post_id of created Post.

        PreparedStatement retrievePostIdQuery = connection.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet retrievePostIdResult = retrievePostIdQuery.executeQuery();

        while (retrievePostIdResult.next()) {
            studentPostId = retrievePostIdResult.getInt(1);
            System.out.println("\nCreated post with post ID: " + "studentPostId.getInt("post_id")");
        }

        //retrievePostIdQuery.close();

        // 5. update the post_id of the created Thread to make the created post the main post.

        PreparedStatement setMainPostQuery = connection.prepareStatement(String.format(
            "INSERT INTO main_post (thread_id, post_id) " +
                        "VALUES(%s, %s);", studentThreadId, studentPostId)
        );
        setMainPostQuery.executeUpdate();
        setMainPostQuery.close();
        connection.commit();
        System.out.println("Connected post to thread...");

        // 6. create PostInFolder using the folderId and postId.
        PreparedStatement setPostFolderQuery = connection.prepareStatement(String.format(
                "INSERT INTO post_in_folder (post_id, folder_id) "
                        + "VALUES(%s, %s);", studentPostId, examFolderId
        ));
        setPostFolderQuery.executeUpdate();
        setPostFolderQuery.close();
        connection.commit();
        System.out.println("Put post in Exam folder...");

        // 7. create PostHasTag using the tagId and postId
        PreparedStatement setPostTagQuery = connection.prepareStatement(String.format(
                "INSERT INTO post_has_tag (post_id, tag_id) " +
                        "VALUES (%s, %s);", studentPostId, tagId
        ));
        setPostTagQuery.executeUpdate();
        setMainPostQuery.close();
        connection.commit();

        System.out.println("Assigned \"Question\" tag to post...");

        System.out.println("Done!\n");
    }


    public void replyToThreadAsInstructor(String postDescription) throws SQLException {
        // 1. create Post using postDescription and the postId + threadId stored as a class variable.
        // CLI class makes sure currentUserEmail corresponds to the instructor email
        PreparedStatement createInstructorReplyQuery = connection.prepareStatement(String.format(
                "INSERT INTO post (post_id, post_description, is_anonymous, last_edited, email, thread_id, self_post_id) " +
                        "VALUES(DEFAULT, \"%s\", TRUE, NOW(), \"%s\", %s, %s);", postDescription, currentUserEmail, studentThreadId, studentPostId
        ));
        createInstructorReplyQuery.executeUpdate();
        createInstructorReplyQuery.close();
        connection.commit();
        System.out.println("Created instructor reply...");
        // 2. retrieve the post_id of the created instructor reply
        PreparedStatement retrievePostIdQuery = connection.prepareStatement("SELECT LAST_INSERT_ID()");
        ResultSet retrievePostIdResult = retrievePostIdQuery.executeQuery();


        while (retrievePostIdResult.next()) {
            instructorPostId = retrievePostIdResult.getInt(1);
            System.out.println("\nCreated instructor's answer to the thread with post_ID: " + instructorPostId.getInt("post_id"));
        }
        retrievePostIdQuery.close();

        // 3. set created Post as Instructor's answer (Thread attribute).
        PreparedStatement setPostAsInstructorAnswerQuery = connection.prepareStatement(String.format(
                "UPDATE thread SET instructor_answer_id = %s WHERE thread_id = %s;", instructorPostId, studentThreadId
        ));
        setPostAsInstructorAnswerQuery.executeUpdate();
        setPostAsInstructorAnswerQuery.close();
        connection.commit();

        System.out.println("Assigns post as the thread's Instructor's answer...");
        System.out.println("\nDone!");
    }


    public void searchForPostByKeyword(String keywordPattern) throws SQLException {
        // 1. Retrieve all matching posts using the LIKE operator
        PreparedStatement retrieveMatchingPostsQuery = connection.prepareStatement(String.format(
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
    }


    public void getUserStatisticsAsInstructor() throws SQLException {
        // 1. Retrieve user statistics from database
        PreparedStatement retrieveStatisticsQuery = connection.prepareStatement(
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
            System.out.println("\n\n---------------------------------------------------------");
            System.out.println("\nUser:           " + "email" + "\n");
            System.out.println("Posts viewed:   " + "viewCount" + "\n");
            System.out.println("Posts created:  " + "createCount" + "\n");
            System.out.println("---------------------------------------------------------\n\n");
        }

        retrieveStatisticsQuery.close();

        System.out.println("\nDone!");
    }
}
