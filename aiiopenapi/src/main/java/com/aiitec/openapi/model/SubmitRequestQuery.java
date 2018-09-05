package com.aiitec.openapi.model;

import com.aiitec.openapi.json.annotation.JSONField;


public class SubmitRequestQuery extends RequestQuery {

    @JSONField(isPassword = true)
    private String password;
    @JSONField(isPassword = true)
    private String passwordNew;
    private int type = -1;
    private int smscodeId = -1;
    private int commentId = -1;
    private int open = -1;
    private long id = -1;
    private String mobile;
    private String message;
    private String content;
    @JSONField(name = "w")
    private BaseWhere where;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public BaseWhere getWhere() {
        return where;
    }

    public void setWhere(BaseWhere where) {
        this.where = where;
    }

    public int getSmscodeId() {
        return smscodeId;
    }

    public void setSmscodeId(int smscodeId) {
        this.smscodeId = smscodeId;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
