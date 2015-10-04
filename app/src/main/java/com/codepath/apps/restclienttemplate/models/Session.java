package com.codepath.apps.restclienttemplate.models;

/**
 * Created by zassmin on 10/3/15.
 */
public class Session {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
