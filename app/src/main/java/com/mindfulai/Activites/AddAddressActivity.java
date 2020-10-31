package com.mindfulai.Activites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mindfulai.Adapter.CityListViewAdapter;
import com.mindfulai.Adapter.StateListViewAdapter;
import com.mindfulai.Models.CityName;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.SPData;
import com.mindfulai.customclass.CityData;
import com.mindfulai.customclass.ExpandableHeightListView;
import com.mindfulai.ministore.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddAddressActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private EditText et_house_num;
    private EditText et_state_address;
    private EditText et_street_address;
    private EditText et_city_address;
    private EditText et_name;
    private EditText et_mobile_number;
    private EditText et_postalcode_address;
    private EditText et_type;
    private String message;
    FusedLocationProviderClient fusedLocationClient;
    private ExpandableHeightListView citylist;
    private ExpandableHeightListView stateList;
    private CityListViewAdapter adapter;
    private StateListViewAdapter stateAdapter;
    private ArrayList<CityName> arraylist;
    private ArrayList<CityName> stateArrayList;
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        startActivityForResult(new Intent(AddAddressActivity.this, MapActivity.class), 101);
        et_house_num = findViewById(R.id.et_house_num);
        et_street_address = findViewById(R.id.et_street_address);
        et_city_address = findViewById(R.id.et_city_address);
        et_state_address = findViewById(R.id.et_state_address);
        et_postalcode_address = findViewById(R.id.et_postal_address);
        et_name = findViewById(R.id.et_name);
        et_mobile_number = findViewById(R.id.et_phone_no);
        if (!SPData.getAppPreferences().getUserName().equals(""))
            et_name.setText(SPData.getAppPreferences().getUserName());
        Button tv_continue = findViewById(R.id.tv_continue);
        setToolbar();
        arraylist = new ArrayList<>();
        stateArrayList = new ArrayList<>();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initializeList();
        initializeListState();

        if (Objects.equals(getIntent().getStringExtra("title"), "Add Address"))
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(AddAddressActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))
                setCurrentAddress();
            else
                ActivityCompat.requestPermissions(AddAddressActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        else
            setUserAddress();
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                    if (validation()) {
                        String name = et_name.getText().toString();
                        String mobile = et_mobile_number.getText().toString();
                        String houseno = et_house_num.getText().toString();
                        String street = et_street_address.getText().toString();
                        String city = et_city_address.getText().toString();
                        String state = et_state_address.getText().toString();
                        String postalCode = et_postalcode_address.getText().toString();
                        customProgressDialog = CommonUtils.showProgressDialog(AddAddressActivity.this, "Please wait.. ");

                        if (getIntent().getStringExtra("title") != null && getIntent().getStringExtra("title").equals("Add Address")) {
                            new SaveProfile().execute(ApiService.GET_ADDRESS, houseno, street, city, state, postalCode, name, mobile);
                        } else {
                            String id = getIntent().getStringExtra("id");
                            String UPDATED_UPDATE_URL = ApiService.UPDATE_ADDRESS + id;
                            new UpdateAddress().execute(UPDATED_UPDATE_URL, houseno, street, city, state, postalCode, name, mobile);
                        }
                    }
                } else {
                    Toast.makeText(AddAddressActivity.this, "Please login to save address", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddAddressActivity.this, LoginActivity.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 101 && data != null){
                Log.i("addAddressAct", "city: "+ data.getStringExtra("city")
                + ", state: " + data.getStringExtra("state") + ", pincode: " + data.getStringExtra("pinCode"));
                et_city_address.setText(data.getStringExtra("city"));
                et_state_address.setText(data.getStringExtra("state"));
                stateList.setVisibility(View.GONE);
                et_postalcode_address.setText(data.getStringExtra("pinCode"));
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    private void setUserAddress() {
        String houseno = getIntent().getStringExtra("houseno");
        String locality = getIntent().getStringExtra("locality");
        String city = getIntent().getStringExtra("city");
        String state = getIntent().getStringExtra("state");
        String pincode = getIntent().getStringExtra("pincode");
        et_house_num.setText(houseno);
        et_street_address.setText(locality);
        et_city_address.setText(city);
        citylist.setVisibility(View.GONE);
        et_state_address.setText(state);
        stateList.setVisibility(View.GONE);
        et_postalcode_address.setText(pincode);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        String title = getIntent().getStringExtra("title");
        toolbar.setTitle("" + title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCurrentAddress();
            }
        }
    }

    private void initializeList() {
        citylist = findViewById(R.id.list_address_city);
        citylist.setVisibility(View.GONE);
        citylist.setExpanded(true);
        for (int i = 0; i < new CityData().getCity().size(); i++) {

            CityName cityName = new CityName(new CityData().getCity().get(i));
            arraylist.add(cityName);
        }
        adapter = new CityListViewAdapter(this, arraylist);
        citylist.setAdapter(adapter);

        citylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_city_address.getWindowToken(), 0);
                citylist.getSelectedItem();
                CityName cityName = (CityName) citylist.getItemAtPosition(position);
                et_city_address.clearFocus();
                et_city_address.setText(cityName.getCityName());
                citylist.setVisibility(View.GONE);
            }
        });


        et_city_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stateList.setVisibility(View.GONE);
                String text = "" + charSequence;
                adapter.filter(text);
                if (!text.isEmpty())
                    citylist.setVisibility(View.VISIBLE);
                else
                    citylist.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initializeListState() {
        stateList = findViewById(R.id.list_address_state);
        stateList.setVisibility(View.GONE);
        stateList.setExpanded(true);
        for (int i = 0; i < new CityData().getState().size(); i++) {
            CityName cityName = new CityName(new CityData().getState().get(i));
            stateArrayList.add(cityName);
        }
        stateAdapter = new StateListViewAdapter(this, stateArrayList);
        stateList.setAdapter(stateAdapter);
        stateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_state_address.getWindowToken(), 0);
                stateList.getSelectedItem();
                CityName cityName = (CityName) stateList.getItemAtPosition(position);
                et_state_address.clearFocus();
                et_state_address.setText(cityName.getCityName());
                stateList.setVisibility(View.GONE);
            }
        });


        et_state_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                citylist.setVisibility(View.GONE);
                String text = "" + charSequence;
                stateAdapter.filter(text);
                if (!text.isEmpty())
                    stateList.setVisibility(View.VISIBLE);
                else
                    stateList.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void setCurrentAddress() {
        try {
            double longitude = Double.parseDouble(SPData.getAppPreferences().getLongitude());
            double latitude = Double.parseDouble(SPData.getAppPreferences().getLongitude());
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(AddAddressActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String locality = addresses.get(0).getSubLocality();
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String pincode = addresses.get(0).getPostalCode();
            et_street_address.setText(locality);
            et_city_address.setText(city);
            citylist.setVisibility(View.GONE);
            et_state_address.setText(state);
            stateList.setVisibility(View.GONE);
            et_postalcode_address.setText(pincode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    class SaveProfile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            JSONObject params = new JSONObject();
            try {
                params.put("addressLine1", strings[1]);
                params.put("addressLine2", strings[2]);
                params.put("city", strings[3]);
                params.put("state", strings[4]);
                params.put("pincode", strings[5]);
                params.put("name", strings[6]);
                params.put("mobile_number", strings[7]);
                RequestBody body = RequestBody.create(mediaType, params.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", SPData.getAppPreferences().getUsertoken())
                        .build();
                Response response = client.newCall(request).execute();
                message = response.message();
                Log.e("TAG", "doInBackground: " + message);
            } catch (Exception e) {
                MDToast.makeText(AddAddressActivity.this, "" + e, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                e.printStackTrace();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonUtils.hideProgressDialog(customProgressDialog);
            if (s.equals("OK"))
                finish();
            else
                Log.e("AddAddressAct", "saveProfile:: postExceute:: failed");
                //Toast.makeText(AddAddressActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class UpdateAddress extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            JSONObject params = new JSONObject();
            try {
                params.put("addressLine1", strings[1]);
                params.put("addressLine2", strings[2]);
                params.put("city", strings[3]);
                params.put("state", strings[4]);
                params.put("pincode", strings[5]);
                params.put("name", strings[6]);
                params.put("mobile_number", strings[7]);
                Log.e("updateAddress", params.toString());
                RequestBody body = RequestBody.create(mediaType, params.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .put(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .addHeader("token", SPData.getAppPreferences().getUsertoken())
                        .build();
                Response response = client.newCall(request).execute();
                message = response.message();
                Log.e("TAG", "doInBackground: " + response.toString());
            } catch (Exception e) {
                MDToast.makeText(AddAddressActivity.this, "" + e, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                e.printStackTrace();
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonUtils.hideProgressDialog(customProgressDialog);
            if (s.equals("OK"))
                finish();
            else
                Log.e("AddAddressAct", "updateAddress:: postExceute:: failed");
            //Toast.makeText(AddAddressActivity.this, "Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validation() {
        //  String type = et_type.getText().toString();
        String houseno = et_house_num.getText().toString();
        String street = et_street_address.getText().toString();
        String city = et_city_address.getText().toString();
        String state = et_state_address.getText().toString();
        String postalCode = et_postalcode_address.getText().toString();
        String name = et_name.getText().toString();
        String phone = et_mobile_number.getText().toString();


        if (TextUtils.isEmpty(houseno.replaceAll(" ", ""))) {
            Toast.makeText(this, "House No can't be empty !!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(street.replaceAll(" ", ""))) {
            Toast.makeText(this, "Street Address can't be empty !!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(city.replaceAll(" ", ""))) {
            Toast.makeText(this, "City can't be empty !!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(state.replaceAll(" ", ""))) {
            Toast.makeText(this, "State can't be empty !!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(postalCode)) {
            Toast.makeText(this, "Postal Code field can't be empty !!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (postalCode.length() != 6) {
            Toast.makeText(this, "Invalid postal code", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.replaceAll(" ", "").isEmpty()) {
            Toast.makeText(this, "Name can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.isEmpty()) {
            Toast.makeText(this, "Mobile no is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (phone.length() < 10) {
            Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
