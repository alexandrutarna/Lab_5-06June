package it.polito.mad.lab5.beans;


public class User {

    private String displayName;
    private String email;
    private String uid;
    private String photoUrl;
    private String instanceId;

    public User() {
    }

    public User(String displayName, String email, String uid, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;

    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}



