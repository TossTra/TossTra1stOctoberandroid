package com.app.tosstra.fragments.dispacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.app.tosstra.R;
import com.app.tosstra.activities.AddANewJobActivity;
import com.app.tosstra.utils.CommonUtils;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.app.tosstra.activities.AddANewJobActivity.current_latlon;
import static com.app.tosstra.activities.AddANewJobActivity.drp_latlon;
import static com.app.tosstra.activities.AddANewJobActivity.et_dropAddval;
import static com.app.tosstra.activities.AddANewJobActivity.et_street;
import static com.app.tosstra.activities.ProfileActivity.etAddress;
import static com.app.tosstra.activities.ProfileActivity.latit;
import static com.app.tosstra.fragments.common.ProfileFragment.etAddress1;
import static com.app.tosstra.fragments.common.ProfileFragment.latit_prof;


public class SearchActivity extends AppCompatActivity {

    private String pickOrDrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        pickOrDrop=getIntent().getStringExtra("pickDrop");

/**
 * Initialize Places. For simplicity, the API key is hard-coded. In a production
 * environment we recommend using a secure mechanism to manage API keys.
 */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBgLeDPeqrgWS2CbGkiO3Gyt5QDwzel1eA");
        }

// Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG);
       // autocompleteFragment.setPlaceFields(fields);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if(pickOrDrop.equalsIgnoreCase("pick")){
                    current_latlon =place.getLatLng();
                    et_street.setText(place.getAddress());
                    Log.e("latlon_curDis", String.valueOf(current_latlon));

                }else if(pickOrDrop.equalsIgnoreCase("drop")) {
                    et_dropAddval.setText(place.getAddress());
                    drp_latlon=place.getLatLng();
                }else if(pickOrDrop.equalsIgnoreCase("profile")){
                    etAddress.setText(place.getAddress());
                    latit=place.getLatLng();
                }else if(pickOrDrop.equalsIgnoreCase("profile_frag")){
                    etAddress1.setText(place.getAddress());
                    latit_prof=place.getLatLng();
                }
                finish();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
                // The user canceled the operation.
            }
        }
    }
}
