package com.rhondasmith.stormy;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rhondasmith.stormy.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Want to set this when we get a successful response
    private CurrentWeather currentWeather;

    // Updating icon
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        // updating setContentView for data binding:
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);

        // Making the Dark Sky attribution link work:
        TextView darkSky = findViewById(R.id.darkSkyAttribution);

        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

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
                            // okHttp string() not to be confused with toString()
                            String jsonData = response.body().string();
                            Log.v(TAG, jsonData);
                            // We don't need this response definition because it is defined
                            // in the onResponse parameters:
                            // Response response = call.execute();
                            if (response.isSuccessful()) {
                                currentWeather = getCurrentDetails(jsonData);

                                // Binding data to our binding variable:
                                CurrentWeather displayWeather = new CurrentWeather(
                                        currentWeather.getLocationLabel(),
                                        currentWeather.getIcon(),
                                        currentWeather.getTemperature(),
                                        currentWeather.getHumidity(),
                                        currentWeather.getPrecipChance(),
                                        currentWeather.getSummary(),
                                        currentWeather.getTime(),
                                        currentWeather.getTimeZone()
                                );

                                // we have to make sure it's declared final since we are using
                                // binding from an inner class
                                binding.setWeather(displayWeather);

                            } else {
                                alertUserAboutError();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "ID Exception caught: ", e);
                        // because the JSONException is thrown by getCurrentDetails which is
                        // being called in the try block here, it can also be caught here:
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Exception caught: ", e);
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

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        // initially have a JSONException
        // can put a try/catch here but it would be better to have it where getCurrentDetails()
        // is called so we 'throw' it there
        JSONObject forecast = new JSONObject(jsonData);

        // Here, making sure that we are getting data from the weather API by logging some of it:
            String timezone = forecast.getString("timezone");
            Log.i(TAG, "From JSON: " + timezone);

            // To get something from a nested JSON object, we have to getJSONObject from the
            // root object:
            JSONObject currently = forecast.getJSONObject("currently");
            // And now we can get strings from the nested JSONObject with getString as before:
            Log.i(TAG, "From JSON: " + currently.getString("summary"));

        // Eventually, we will return an instance of the  CurrentWeather class
        // Before it was null just so we wouldn't get an error for not having a return value
        // We can set values for our data model using actual data from the weather API:
        // Instead of having a bunch of log statements to check these, it's easier to check
        // values by running the app with the debugger/a break point at CurrentWeather
        // and stepping through the CurrentWeather object to see the values as they come in:
        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setLocationLabel("Alcatraz Island, CA");
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        // because we added timezone to our data model, and are using it,  need to set a value
        // No more error because we added timezone property + getter & setter in CurrentWeather
        currentWeather.setTimeZone(timezone);

        // Checking on our human readable time:
        Log.d(TAG, "HUMAN TIME: " + currentWeather.getFormattedTime());

        return currentWeather;
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
