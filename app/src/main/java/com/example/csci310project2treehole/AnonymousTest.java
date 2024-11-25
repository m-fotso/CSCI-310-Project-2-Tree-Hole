package com.example.csci310project2treehole;
import android.os.Bundle;
import android.util.Log;
import java.util.Vector;

public class AnonymousTest {
    Boolean result;
    protected void onCreate(Bundle savedInstanceState) {
        //testAnonymousReply();
        //Log.d("Mytag", "Test result was" + result.toString());
    }

    public void testInvalidPost() {
        Post post1 = new Post("id", "author", "name", "", "content", 0L, true);
        Post post2 = new Post("id", "author", "name", "title", "", 0L, true);
        Post post3 = new Post("id", "author", "name", "", "", 0L, true);
        assertEquals(post1);
        assertEquals(post2);
        assertEquals(post3);
    }
    public void testAnonymousNameNoChange() {
        Post post1 = new Post("id", "author", "name", "title", "content", 0L, true);
        Post post2 = new Post("id", "author", "name", "title", "content", 0L, true);
        Post post3 = new Post("id", "author", "name", "title", "content", 0L, true);
        Vector<String> list = new Vector<String>();
        String name1 = post1.getAnonymousNameForUser("user1");
        String name2 = post2.getAnonymousNameForUser("user1");
        String name3 = post3.getAnonymousNameForUser("user1");
        list.add(name1);
        list.add(name2);
        list.add(name3);
        assertEquals(list, "Anonymous 1");
    }

    public void testAnonymousReply() {
        Post post = new Post("id", "author", "name", "", "content", 0L, true);
        Reply reply = new Reply("id1", "id","author1", "content", 0L, true, "id", "author");
        String name = reply.getAnonymousName();
        result = assertEquals(name, "Anonymous 1");
    }
    public Boolean assertEquals(String actual, String expected) {
        return (actual.equals(expected));
    }

    public Boolean assertEquals(Vector<String> actual, String expected) {
        for(int i=0; i<actual.size(); i++) {
            if(!actual.get(i).equals(expected)) {
                return false;
            }
        }
        return true;
    }

    public Boolean assertEquals(Post post) {
        return (post == null);
    }
}
