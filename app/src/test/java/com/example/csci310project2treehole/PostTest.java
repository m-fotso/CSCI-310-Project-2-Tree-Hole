package com.example.csci310project2treehole;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

/**
 * White-box unit tests written by Zach Dodson
 */
public class PostTest {
    private Post testPost;
    private static final String TEST_POST_ID = "test_post_id";
    private static final String TEST_AUTHOR_ID = "test_author_id";

    @Before
    public void setUp() {
        testPost = new Post(TEST_POST_ID, TEST_AUTHOR_ID, "Test Author",
                "Test Title", "Test Content", System.currentTimeMillis(), false);
    }

    @Test
    public void testPostCreationWithAnonymity() {
        Post anonymousPost = new Post(TEST_POST_ID, TEST_AUTHOR_ID, "Test Author",
                "Anonymous Title", "Content", System.currentTimeMillis(), true);
        assertTrue(anonymousPost.isAnonymous());
        assertEquals("Anonymous 1", anonymousPost.getAnonymousNameForUser(TEST_AUTHOR_ID));
    }

    @Test
    public void testMultipleAnonymousUsers() {
        testPost.setAnonymous(true);
        String user1 = "user1";
        String user2 = "user2";

        String anonName1 = testPost.getAnonymousNameForUser(user1);
        String anonName2 = testPost.getAnonymousNameForUser(user2);

        assertNotEquals(anonName1, anonName2);
        assertEquals(anonName1, testPost.getAnonymousNameForUser(user1));
    }

    @Test
    public void testAnonymityConsistency() {
        String userId = "test_user";
        testPost.setUserAnonymousStatus(userId, true);

        assertTrue(testPost.shouldUserBeAnonymous(userId));
        assertEquals(testPost.getAnonymousNameForUser(userId),
                testPost.getDisplayNameForUser(userId, "Real Name"));
    }

    @Test
    public void testReplyManagement() {
        Reply reply = new Reply("reply1", TEST_AUTHOR_ID, "Author",
                "Reply content", System.currentTimeMillis(), false, null, null);

        Map<String, Reply> replies = testPost.getReplies();
        replies.put("reply1", reply);
        testPost.setReplies(replies);

        assertEquals(1, testPost.getReplies().size());
        assertEquals(reply, testPost.getReplies().get("reply1"));
    }

    @Test
    public void testCategoryAssignment() {
        testPost.setCategory("Academic");
        assertEquals("Academic", testPost.getCategory());

        testPost.setCategory("Life");
        assertEquals("Life", testPost.getCategory());

        testPost.setCategory("Event");
        assertEquals("Event", testPost.getCategory());
    }
}