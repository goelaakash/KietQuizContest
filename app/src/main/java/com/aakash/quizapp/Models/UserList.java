package com.aakash.quizapp.Models;


public class UserList {
    public String gender;
    public String image;
    public String name;
    public String userID;
    public String emailid;


    public UserList() {
    }

    public UserList(String Name, String UserID, String Gender, String Image, String emailid) {
        this.name = Name;
        this.userID = UserID;
        this.emailid=emailid;
        this.image = Image;
        this.gender = Gender;
    }
    public UserList(String UserID) {
        this.userID = UserID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGender(){
        return gender;
    }
    public String getImage(){
        return image;
    }
    public String getName(){
        return name;
    }
    public String getUserID(){
        return userID;
    }

}
