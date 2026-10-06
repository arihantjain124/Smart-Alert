package com.idk.smartalert;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

/** Sends administrator alerts through a trusted server-side endpoint. */
public class adminalert extends AppCompatActivity {
    private static final String TAG = "NOTIFICATION TAG";
    private final String alertsEndpoint = BuildConfig.ALERTS_ENDPOINT;

    private EditText title;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminalert);

        title = findViewById(R.id.editText3);
        message = findViewById(R.id.editText2);
        Button sendButton = findViewById(R.id.button);
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAlert();
            }
        });
    }

    private void sendAlert() {
        if (alertsEndpoint.isEmpty()) {
            Toast.makeText(this, "Configure ALERTS_ENDPOINT before sending alerts.", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject data = new JSONObject();
        JSONObject requestBody = new JSONObject();
        try {
            data.put("title", title.getText().toString().trim());
            data.put("message", message.getText().toString().trim());
            data.put("topic", "news");
            requestBody.put("data", data);
        } catch (JSONException exception) {
            Log.e(TAG, "Could not build alert payload", exception);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                alertsEndpoint,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Alert accepted: " + response);
                        title.setText("");
                        message.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Alert request failed", error);
                        Toast.makeText(adminalert.this, "Could not send alert.", Toast.LENGTH_LONG).show();
                    }
                }
        );
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
