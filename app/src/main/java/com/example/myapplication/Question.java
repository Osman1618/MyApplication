package com.example.myapplication;

public class Question
{
    public String uid, title, time, questionImage, fullname, date, body, coursecode ;

    public Question (){

    }

    public Question(String uid, String title, String time, String questionImage, String fullname, String date, String body, String coursecode) {
        this.uid = uid;
        this.title = title;
        this.time = time;
        this.questionImage = questionImage;
        this.fullname = fullname;
        this.date = date;
        this.body = body;
        this.coursecode = coursecode;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(String coursecode) {
        this.coursecode = coursecode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
