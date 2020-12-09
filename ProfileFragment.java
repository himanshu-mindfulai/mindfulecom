package com.mindfulai.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.mindfulai.Activites.AddAddressActivity;
import com.mindfulai.Activites.LoginActivity;
import com.mindfulai.Activites.ProfileActivity;
import com.mindfulai.Adapter.UserAddressesProfileAdapter;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.CustomerInfo.CustomerData;
import com.mindfulai.Models.UserBaseAddress;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.CustomProgressDialog;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileActivity";
    private EditText et_name, et_email;
    private String name;
    private String profile;
    private CircleImageView profile_image;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 2;
    private MultipartBody.Part filePart;
    private AppPreferences appPreferences;
    private RecyclerView recyclerViewAddress;
    private ArrayList<UserDataAddress> userDataAddressArrayList;
    private UserAddressesProfileAdapter addressesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        appPreferences = new AppPreferences(getActivity());
        et_name = view.findViewById(R.id.et_name);
        et_email = view.findViewById(R.id.et_email);
        Button tv_continue = view.findViewById(R.id.tv_continue);
        profile_image = view.findViewById(R.id.profile_image);
        userDataAddressArrayList = new ArrayList<>();
        TextView addAddress = view.findViewById(R.id.add_address);
        if (!SPData.getAppPreferences().getUsertoken().equals("")) {
            showProfileData();
            getAllAddress();
        }
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                    startActivityForResult(new Intent(getActivity(), AddAddressActivity.class).putExtra("title", "Add Address"), 3);
                } else {
                    Toast.makeText(getActivity(), "Please login to add address", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), 3);
                }

            }
        });
        recyclerViewAddress = view.findViewById(R.id.recycler_view_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAddress.setLayoutManager(linearLayoutManager);
        tv_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                    if (!et_name.getText().toString().replaceAll(" ", "").isEmpty()
                            && !et_email.getText().toString().isEmpty()) {
                        uploadImage();
                    } else
                        MDToast.makeText(getActivity(), "Name or email can't be empty", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();

                } else {
                    Toast.makeText(getActivity(), "Please login to save profile", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), 3);
                }
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    askForGalleryAndCamera();
                } else {
                    requestPermission();
                }
            }
        });
        return view;
    }
    private void getAllAddress() {
        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(getActivity(),
                "Getting addresses ... ");
        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());
        apiService.getUserBaseAddress().enqueue(new Callback<UserBaseAddress>() {
            @Override
            public void onResponse(@NonNull Call<UserBaseAddress> call, @NonNull Response<UserBaseAddress> response) {
                if (response.isSuccessful()) {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    assert response.body() != null;
                    userDataAddressArrayList = response.body().getData();
                    addressesAdapter = new UserAddressesProfileAdapter(getActivity(), userDataAddressArrayList);
                    recyclerViewAddress.setAdapter(addressesAdapter);
                    addressesAdapter.notifyDataSetChanged();
                } else {
                    CommonUtils.hideProgressDialog(customProgressDialog);
                    Log.e(TAG, "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(Call<UserBaseAddress> call, Throwable t) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int resultReadExternal = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && resultReadExternal == PackageManager.PERMISSION_GRANTED;
    }

    final int cameraAndGalleryIntent = 100;

    private void askForGalleryAndCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = Intent.createChooser(galleryIntent, "Some text here");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        startActivityForResult(chooser, cameraAndGalleryIntent);
    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
            openPermissionIntent();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }


    private void openPermissionIntent() {
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    Boolean isprofilePicChanges = false;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == cameraAndGalleryIntent) {

                if (data != null) {
                    isprofilePicChanges = true;

                    if (data.getData() != null) {
                        //gallery
                        handleGalleryData(data);
                    } else {
                        //camera
                        handleCameraData(data);
                    }

                }

            } else if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri treeUri = data.getData();
                    int takeFlags = data.getFlags();
                    takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    assert treeUri != null;
                    getActivity().getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

                }
            }
        } else if (requestCode == 3) {
            if (!SPData.getAppPreferences().getUsertoken().equals("")) {
                showProfileData();
                getAllAddress();
            }
        }
    }


    Uri galleryImageUri;
    Boolean isGallery = false;
    File selectedFile;

    private void handleGalleryData(Intent data) {
        isGallery = true;


        Uri uri = data.getData();

        Glide.with(getActivity())
                .load(uri)
                .into(profile_image);


        galleryImageUri = uri;

        selectedFile = new File(getRealPathFromURI(uri));
    }

    private void handleCameraData(Intent data) {


        Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        profile_image.setImageBitmap(thumbnail);

        selectedFile = destination;
    }

    private void showProfileData() {

        ApiService apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().getUsertoken());

        apiService.getProfileDetails().enqueue(new Callback<CustomerData>() {
            @Override
            public void onResponse(@NonNull Call<CustomerData> call, @NonNull Response<CustomerData> response) {

                try {
                    if (response.isSuccessful()) {

                        CustomerData customerData;
                        customerData = response.body();

                        assert customerData != null;
                        SPData.getAppPreferences().setUserName(customerData.getData().getUser().getFullName());
                        SPData.getAppPreferences().setUserProfilePic(customerData.getData().getUser().getProfilePicture());
                        SPData.getAppPreferences().setUser_mobile_no(customerData.getData().getUser().getMobileNumber());
                        SPData.getAppPreferences().setEmail(customerData.getData().getUser().getEmail());
                        name = customerData.getData().getUser().getFullName();
                        et_name.setText(CommonUtils.capitalizeWord(name));
                        et_email.setText(customerData.getData().getUser().getEmail());
                        profile = customerData.getData().getUser().getProfilePicture();
                        if (profile != null && !profile.isEmpty()) {
                            Glide.with(getActivity()).load(GlobalEnum.AMAZON_URL + profile).into(profile_image);
                        } else {
                            Glide.with(getActivity()).load(getResources().getDrawable(R.drawable.user)).into(profile_image);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CustomerData> call, @NonNull Throwable t) {
                Log.e("fail", call.toString());
                Toast.makeText(getActivity(), "Failed to connect. " + " Please reload", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void uploadImage() {

        final CustomProgressDialog customProgressDialog = CommonUtils.showProgressDialog(getActivity(), "Uploading Image.. ");
        ApiService apiService = ApiUtils.getImageAPIService(SPData.getAppPreferences().getUsertoken());
        RequestBody requestFile =
                null;
        try {
            if (isGallery) {
                requestFile = RequestBody.create(
                        MediaType.parse(Objects.requireNonNull(requireActivity().getContentResolver().getType(galleryImageUri))),
                        selectedFile
                );
            } else {
                requestFile = RequestBody.create(
                        MediaType.parse(Objects.requireNonNull(requireActivity().getContentResolver().getType(convertFileToContentUri(selectedFile)))),
                        selectedFile
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "uploadImage: " + e);
        }

        if (selectedFile != null) {
            if (requestFile != null) {
                filePart = MultipartBody.Part.createFormData("profile_picture", selectedFile.getName(), requestFile);
            }
        }

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), et_name.getText().toString());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), et_email.getText().toString());
        RequestBody mobileNumber = RequestBody.create(MediaType.parse("text/plain"), appPreferences.getMobileNumber());
        Call<JsonObject> call = apiService.uploadFile(filePart, name, email, mobileNumber);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                if (response.isSuccessful()) {
                    SPData.getAppPreferences().setUserName("" + et_name.getText().toString());
                    SPData.getAppPreferences().setEmail("" + et_email.getText().toString());
                    SPData.getAppPreferences().setUserProfilePic(response.body().getAsJsonObject("data").get("profile_picture").toString());
                    showProfileData();
                    Toast.makeText(getActivity(), "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "onResponse: " + response);
                    Toast.makeText(getActivity(), "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                CommonUtils.hideProgressDialog(customProgressDialog);
                Log.e("TAG", "onFailure: " + t.getMessage());
            }
        });

    }

    protected Uri convertFileToContentUri(File file) throws Exception {
        ContentResolver cr = getActivity().getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        return Uri.parse(uriString);
    }
}
