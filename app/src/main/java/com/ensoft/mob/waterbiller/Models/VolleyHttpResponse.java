package com.ensoft.mob.waterbiller.Models;

import org.json.JSONObject;

/**
 * Created by Ebuka on 06/09/2015.
 */
public class VolleyHttpResponse {
    int statusCode;
    String successResponse;
    JSONObject successJSONResponse;
    String errorResponse;

    public void setStatusCode(int _statusCode)
    {
        this.statusCode = _statusCode;
    }
    public void setSuccessResponse(String _successResponse)
    {
        this.successResponse = _successResponse;
    }
    public void setErrorResponse(String _errorResponse)
    {
        this.errorResponse = _errorResponse;
    }
    public void setSuccessJSONResponse(JSONObject _response)
    {
        this.successJSONResponse = _response;
    }


    public int getStatusCode()
    {
        return statusCode;
    }
    public String getSuccessResponse()
    {
        return successResponse;
    }
    public String getErrorResponse()
    {
        return errorResponse;
    }
    public JSONObject getSuccessJSONResponse()
    {
        return successJSONResponse;
    }
}
