package com.njit.buddy.application.network.task;

import android.os.AsyncTask;
import android.util.Log;
import com.njit.buddy.application.network.Connector;
import com.njit.buddy.application.network.ResponseCode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author toyknight 4/10/2016.
 */
public abstract class PasswordChangeTask extends AsyncTask<String, Void, Integer> implements ResponseHandler<Integer> {

    @Override
    protected Integer doInBackground(String... params) {
        String old_password = params[0];
        String new_password = params[1];

        try {
            JSONObject request_body = new JSONObject();
            request_body.put("old_password", old_password);
            request_body.put("new_password", new_password);

            String result = Connector.executePost(Connector.SERVER_ADDRESS + "/password/change", request_body.toString());
            JSONObject response = new JSONObject(result);
            return response.getInt("response_code");
        } catch (JSONException ex) {
            Log.d("Password Change", ex.toString());
            return ResponseCode.SERVER_ERROR;
        } catch (IOException ex) {
            Log.d("Password Change", ex.toString());
            return ResponseCode.SERVER_ERROR;
        }
    }

    @Override
    protected final void onPostExecute(Integer response_code) {
        if (response_code == ResponseCode.BUDDY_OK) {
            onSuccess(response_code);
        } else {
            onFail(response_code);
        }
    }

}
