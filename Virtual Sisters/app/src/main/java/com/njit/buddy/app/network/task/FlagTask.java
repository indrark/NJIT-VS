package com.njit.buddy.app.network.task;

import android.os.AsyncTask;
import android.util.Log;
import com.njit.buddy.app.network.Connector;
import com.njit.buddy.app.network.ResponseCode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author toyknight 11/19/2015.
 */
public abstract class FlagTask extends AsyncTask<Integer, Void, Integer> implements ResponseHandler<Integer> {

    @Override
    protected Integer doInBackground(Integer... params) {
        Integer pid = params[0];

        try {
            JSONObject request_body = new JSONObject();
            request_body.put("pid", pid);

            String result = Connector.executePost(Connector.SERVER_ADDRESS + "/post/flag", request_body.toString());
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
