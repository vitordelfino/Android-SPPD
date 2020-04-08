package com.app.sppd.Interfaces;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by vitor on 07/02/17.
 */

public interface VolleyCallbackObject {
    void onSuccess(JSONObject result) throws JSONException, IOException;
}
