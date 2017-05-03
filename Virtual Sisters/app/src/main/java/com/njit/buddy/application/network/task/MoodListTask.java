package com.njit.buddy.application.network.task;

import android.os.AsyncTask;
import android.util.Log;
import com.njit.buddy.application.entity.Mood;
import com.njit.buddy.application.network.Connector;
import com.njit.buddy.application.network.ResponseCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Indraneel on 4/11/2017.
 */

public abstract class MoodListTask
        extends AsyncTask<Integer, Void, JSONObject> implements ResponseHandler<ArrayList<Mood>> {

    @Override
    protected JSONObject doInBackground(Integer... params) {
        Integer page = params[0];

        try {
            JSONObject request_body = new JSONObject();
            request_body.put("page", page);
            ;

            String result = Connector.executePost(Connector.SERVER_ADDRESS + "/mood/list", request_body.toString());
            return new JSONObject(result);
        } catch (JSONException ex) {
            Log.d("JSON", ex.toString());
            return null;
        } catch (IOException ex) {
            Log.d("Network", ex.toString());
            return null;
        }
    }

    @Override
    protected final void onPostExecute(JSONObject result) {
        if (result == null) {
            onFail(ResponseCode.SERVER_ERROR);
        } else {
            try {
                int response_code = result.getInt("response_code");
                if (response_code == ResponseCode.BUDDY_OK) {
                    JSONArray posts = result.getJSONArray("moods");
                    ArrayList<Mood> mood_list = new ArrayList<Mood>();
                    for (int i = 0; i < posts.length(); i++) {
                        JSONObject element = posts.getJSONObject(i);
                        Mood mood = new Mood(element.getLong("timestamp"), element.getInt("mood"));
                        mood_list.add(mood);
                    }
                    onSuccess(mood_list);
                } else {
                    onFail(response_code);
                }
            } catch (JSONException ex) {
                Log.d("Error", ex.toString());
                onFail(ResponseCode.SERVER_ERROR);
            }
        }
    }

}
