package com.ohmshantiapps.menu;
public class WithdrawalRequest {
    private String userId;
    private String userName;
    private int amount;
    private String status;
    private String time;
    private String detail;



    public WithdrawalRequest(String userId, String userName, int amount, String status, String time,String detail) {
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
        this.detail=detail;
        this.status = status;
        this.time = time;
    }


    // Getters and Setters
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}


