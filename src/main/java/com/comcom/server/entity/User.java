package com.comcom.server.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {


    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(!isValidEmailAddress(email)){
            throw new RuntimeException("invalid email id");
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public ObjectId getId() {
        return id;
    }

    public void setNotificationDetails(NotificationDetails notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public NotificationDetails getNotificationDetails(){
        return notificationDetails;
    }

    @Id
    private ObjectId id;
    private boolean admin;
    private boolean verified;
    private String username;
    private String fullname;
    private String email;
    private String password;
    private String token;
    private NotificationDetails notificationDetails;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", admin=" + admin +
                ", verified=" + verified +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ((notificationDetails==null)?"": (", notificationDetails=" + notificationDetails.toString())) +
                '}';
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }



    public enum OsType{
        android,ios
    }

    public static class NotificationDetails{
        private ArrayList<DeviceTokenParams> detailslist;

        public ArrayList<DeviceTokenParams> getDetailslist() {
            return detailslist;
        }

        public void setDetailslist(ArrayList<DeviceTokenParams> detailslist) {
            this.detailslist = detailslist;
        }



        public static class DeviceTokenParams{
            private String deviceId;
            private OsType osType;

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public OsType getOsType() {
                return osType;
            }

            public void setOsType(OsType osType) {
                this.osType = osType;
            }

            @Override
            public String toString() {
                return "DeviceTokenParams{" +
                        "deviceId='" + deviceId + '\'' +
                        ", osType=" + osType +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "NotificationDetails{" +
                    "detailslist=" + detailslist.toString() +
                    '}';
        }
    }
}
