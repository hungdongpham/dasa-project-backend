package com.project.ws.Model;

import org.json.simple.JSONObject;

public class ResponseMessage {
    int status;
    JSONObject response;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public ResponseMessage(int status, JSONObject response) {
        this.status = status;
        this.response = response;
    }
}
