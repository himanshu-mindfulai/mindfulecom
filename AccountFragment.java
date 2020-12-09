package com.mindfulai.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mindfulai.Activites.AboutActivity;
import com.mindfulai.Activites.FAQActivity;
import com.mindfulai.Activites.LoginActivity;
import com.mindfulai.Activites.OrderHistoryActivity;
import com.mindfulai.Activites.PrivacyPolicy;
import com.mindfulai.Activites.ProfileActivity;
import com.mindfulai.Models.CustomerInfo.CustomerData;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.NetworkRetrofit.ApiUtils;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {


    private CardView cardViewLogout,cardViewLogin,cardViewEdit;
    private String name, mobile, address, profile;
    private static final String TAG = "AccountFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.account_fragment, container, false);

        try {
            cardViewEdit = root.findViewById(R.id.cardViewEdit);
            cardViewLogout = root.findViewById(R.id.cardViewLogout);
            cardViewLogin=root.findViewById(R.id.cardViewLogin);
            CardView cardViewOrderHistory = root.findViewById(R.id.cardViewOrderhistory);
            CardView cardViewAbout = root.findViewById(R.id.cardViewAbout);
            CardView cardViewFAQ = root.findViewById(R.id.cardViewFAQ);
            CardView cardViewPrivacy = root.findViewById(R.id.cardViewPrivacyPolicy);
            CardView cardViewFeedback = root.findViewById(R.id.cardViewFeedback);

            cardViewOrderHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
                }
            });
            cardViewAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), AboutActivity.class));
                }
            });
            cardViewFAQ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), FAQActivity.class));
                }
            });
            cardViewPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PrivacyPolicy.class));
                }
            });
            cardViewFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToGmail("Feedback", SPData.emailAddress());

                }
            });

            cardViewEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getContext(), ProfileActivity.class);
                    startActivity(i);


                }
            });
            cardViewLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), LoginActivity.class);
                    startActivity(i);
                }
            });

            cardViewLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showPopup();

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreateView: " + e);
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SPData.getAppPreferences().getUsertoken().equals("")) {
            cardViewLogout.setVisibility(View.GONE);
            cardViewLogin.setVisibility(View.VISIBLE);
            cardViewEdit.setVisibility(View.GONE);
        } else{
            cardViewLogout.setVisibility(View.VISIBLE);
            cardViewLogin.setVisibility(View.GONE);
            cardViewEdit.setVisibility(View.VISIBLE);
        }
    }

    private void sendToGmail(String subject,String email) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.CATEGORY_APP_EMAIL, true);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        final PackageManager pm = getActivity().getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(intent);
    }

    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Are you sure you wnat to Logout?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        logout(); // Last step. Logout function

                    }
                }).setNegativeButton("Cancel", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void logout() {
        SPData.getAppPreferences().clearAppPreference();
        startActivity(new Intent(getContext(), LoginActivity.class).putExtra("from", "logout"));
    }

}
