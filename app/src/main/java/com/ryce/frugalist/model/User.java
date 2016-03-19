package com.ryce.frugalist.model;

import com.ryce.frugalist.network.FrugalistResponse;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tony on 2016-02-06.
 *
 * Presentation layer user model
 */
public class User {

    private String facebookId;
    private String name;
    private Set<Long> bookmarks;

    public User(String facebookId, String name, Set<Long> bookmarks) {
        this.facebookId = facebookId;
        this.name = name;
        this.bookmarks = new HashSet<>(bookmarks);
    }

    /**
     * To convert a response User to a view model User
     * @param user
     */
    public User(FrugalistResponse.User user) {
        this.facebookId = user.id;
        this.name = user.name;
        this.bookmarks = user.bookmarks == null ?
                new HashSet<Long>() :
                new HashSet<Long>(user.bookmarks);
    }

    public Set<Long> getBookmarks() {
        return bookmarks;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
