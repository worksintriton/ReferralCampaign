package com.triton.referralcampaign.responsepojo;

public class SuccessResponse {


    /**
     * Status : Success
     * Message : Update Referral Code
     * Data : https://test2-37b23-default-rtdb.firebaseio.com/Files/data/userDetails
     * Code : 200
     */

    private String Status;
    private String Message;
    private String Data;
    private int Code;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getData() {
        return Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }
}
