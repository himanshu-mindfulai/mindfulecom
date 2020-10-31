package com.mindfulai.Models;

import android.webkit.SafeBrowsingResponse;

public class NotificationModel {
    private String tag;
    private String type;
    private String image;
    private String title;
    private String body;

    public NotificationModel(String tag, String type, String image, String title, String body) {
        this.tag = tag;
        this.type = type;
        this.image = image;
        this.title = title;
        this.body = body;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
