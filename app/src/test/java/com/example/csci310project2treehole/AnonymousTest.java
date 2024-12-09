package com.example.csci310project2treehole;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AnonymousTest {

    @Test
    public void testInvalidPost() {
        Post post1 = new Post("id", "author", "name", "", "content", 0L, true);
        assertNull("Expect post without title to return null",post1);
        Post post2 = new Post("id", "author", "name", "title", "", 0L, true);
        assertNull("Expect post without content to return null", post2);
        Post post3 = new Post("id", "author", "name", "", "", 0L, true);
        assertNull("Expect post without title and content to return null", post3);
    }

    @Test
    public void testAnonymousNameNoChangeAfterNextPost() {
        Post post1 = new Post("id", "author", "name", "title", "content", 0L, true);
        Post post2 = new Post("id", "author", "name", "title", "content", 0L, true);
        String name1 = post1.getAnonymousNameForUser("user1");
        String name2 = post2.getAnonymousNameForUser("user1");
        assertEquals("Expect name of user to stay the same for both posts",name1, name2);
    }

    public void testAnonymousReply() {
        Post post = new Post("id", "author", "name", "", "content", 0L, true);
        Reply reply = new Reply("id1", "id","author1", "content", 0L, true, "id", "author");
        String name = reply.getAnonymousName();
        assertNotNull("Expect reply to have the name of an anonymous user", name);
    }
}
