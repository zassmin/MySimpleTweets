package com.codepath.apps.restclienttemplate.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by zassmin on 9/30/15.
 */
@Table(name = "users")
public class User extends Model implements Serializable {
    @Column(name = "name")
    private String name;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long remoteId;
    @Column(name = "screen_name")
    private String screenName;
    @Column(name = "profile_image")
    private String profileImageUrl;

    public String getName() {
        return name;
    }
    public long getRemoteId() {
        return remoteId;
    }
    public String getScreenName() {
        return screenName;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public static User findOrSetScreenNameFromJSON(JSONObject json) {
        String sN = "";
        try {
            sN = json.getString("screen_name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user;
        user = new Select().from(User.class).where("screen_name = ?", sN).executeSingle();
        if (user != null && user.getScreenName() != null) {
            return user;
        }

        user = new User();
        user.setScreenName(sN);
        user.save();

        return user;
    }

    // deserialize user json
    public static User findOrCreateFromJson(JSONObject json) {
        long rId = 0;
        try {
            rId = json.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        User u;
        // find user first
        u = new Select().from(User.class).where("remote_id = ?", rId).executeSingle();
        if (u != null) {
            return u;
        }
        // otherwise create user
        try {
            u = new User();
            u.name = json.getString("name");
            u.remoteId = json.getLong("id");
            u.profileImageUrl = json.getString("profile_image_url");
            u.screenName = json.getString("screen_name");
            u.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }
}
