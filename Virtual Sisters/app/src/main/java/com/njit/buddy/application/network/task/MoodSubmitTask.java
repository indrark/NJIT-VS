package com.njit.buddy.application.network.task;


import android.os.AsyncTask;
import android.util.Log;
import com.njit.buddy.application.network.Connector;
import com.njit.buddy.application.network.ResponseCode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Indraneel on 4/11/2017.
 */

public abstract class MoodSubmitTask extends AsyncTask<Integer, Void, Integer> implements ResponseHandler<Integer> {

    @Override
    protected Integer doInBackground(Integer... params) {
        Integer mood = params[0];

        try {
            JSONObject request_body = new JSONObject();
            request_body.put("mood", mood);

            String result = Connector.executePost(Connector.SERVER_ADDRESS + "/mood/submit", request_body.toString());
            JSONObject response = new JSONObject(result);
            return response.getInt("response_code");
        } catch (JSONException ex) {
            Log.d("Network", ex.toString());
            return ResponseCode.SERVER_ERROR;
        } catch (IOException ex) {
            Log.d("Network", ex.toString());
            return ResponseCode.SERVER_ERROR;
        }
    }

    @Override
    protected final void onPostExecute(Integer response_value) {
        if (response_value == ResponseCode.BUDDY_OK) {
            onSuccess(response_value);
        } else {
            onFail(response_value);
        }
    }

}