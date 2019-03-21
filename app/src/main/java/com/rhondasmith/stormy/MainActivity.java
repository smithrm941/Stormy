package com.rhondasmith.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GET the weather data:
            String apiKey = "534a82b17a110315d631fe419efc5d82";

            double latitude = 37.8267; // use something like 99999 to force error for test
            double longitude = -122.4233;
            String forecastURL = "https://api.darksky.net/forecast/"
                    + apiKey + "/" + latitude +  "," + longitude;

            // in the isNetworkAvailable() method in this class,
            // we set a return value based on network availability
            if(isNetworkAvailable()) {
                // Our main client object:
                OkHttpClient client = new OkHttpClient();

                // A request for the client to send to the Dark Sky server:
                Request request = new Request.Builder()
                        .url(forecastURL)
                        .build();

                Call call = client.newCall(request);

                // the queue method is provided by OkHttp and executes calls asynchronously
                // with one call in the queue, it is executed right away
                // this code is being executed in the background while we can continue
                // to execute code on the main UI thread
                call.enqueue(new Callback() {
                    // this is an anonymous inner class, of type Callback with two methods to override:
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Response from Dark Sky:
                        try {
                            //Response response = call.execute();
                            if (response.isSuccessful()) {
                                // okHttp string() not to be confused with toString()
                                Log.v(TAG, response.body().string());
                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "ID Exception caught: ", e);
                        }

                    }
                });

                // If this was synchronous code, this wouldn't show up until the response finished
                // But with asynchronous code, this runs first while the response is happening in
                // the background and executes last, when it's done
                // asynchronous processing is a bridge between background thread and the main thread
            }
            Log.d(TAG, "Main UI code is running, hooray!");

    }

    private boolean isNetworkAvailable() {
        // We have to cast getSystemService(Context.CONNECTIVITY_SERVICE) to ConnectivityManger
        // Because getSystemService returns a generic object so we have to cast it to the
        // ConnectivityManager type
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Like the internet permission, we also had to add permission to access the network state
        // in the manifest before this line of code worked:
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        // we check the network state (networkInfo) and if it's connected, return true
        // and allow the request/response cycle to go through (see 'if(isNetworkAvailable)'):
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        // we want a simple message for the user in case the network ISN'T available:
        else {
            // another way to have an error pop up.
            // we could also use an alert if we wanted:
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
            // alertUserAboutError();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_dialog");
    }

}
