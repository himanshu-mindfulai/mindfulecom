package com.mindfulai.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mindfulai.Activites.AboutActivity;
import com.mindfulai.Activites.FAQActivity;
import com.mindfulai.Activites.LoginActivity;
import com.mindfulai.Activites.MainActivity;
import com.mindfulai.Activites.PrivacyPolicy;
import com.mindfulai.Activites.ProfileActivity;
import com.mindfulai.Activites.WalletActivity;
import com.mindfulai.NetworkRetrofit.ApiService;
import com.mindfulai.Utils.SPData;
import com.mindfulai.dao.AppDatabase;
import com.mindfulai.ministore.R;

import java.util.List;

public class MoreFragment extends Fragment {

    private CardView cardViewLogout;
    private CardView cardViewProfile;
    private TextView textView1;
    private TextView textView2;
    private CardView cardViewWallet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        cardViewProfile = view.findViewById(R.id.card_profile);
        CardView cardViewAbout = view.findViewById(R.id.card_about);
        CardView cardViewContact = view.findViewById(R.id.card_contact);
        CardView cardViewFaq = view.findViewById(R.id.card_faq);
        cardViewLogout = view.findViewById(R.id.card_logout);
        CardView cardViewPrivacy = view.findViewById(R.id.card_privacy);
        CardView cardViewTerms = view.findViewById(R.id.card_terms_condition);
        CardView cardViewReturn = view.findViewById(R.id.card_return_privacy);
        CardView cardViewReport = view.findViewById(R.id.card_report_prblm);
        CardView cardViewRate = view.findViewById(R.id.card_rate);
        CardView cardViewShare = view.findViewById(R.id.card_share);
        cardViewWallet = view.findViewById(R.id.card_wallet);
        textView1 = view.findViewById(R.id.login_logout);
        textView2 = view.findViewById(R.id.login_logout_msg);
        cardViewWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WalletActivity.class));
            }
        });
        cardViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateShareLink();
            }
        });
        cardViewRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        cardViewFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FAQActivity.class));
            }
        });
        cardViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToGmail("Bug/Issue in Ecom", SPData.emailAddress());
            }
        });
        cardViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });
        cardViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });
        cardViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToGmail("Info/Help regarding " + getString(R.string.app_name), SPData.emailAddress());
            }
        });
        cardViewFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FAQActivity.class));
            }
        });
        cardViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SPData.getAppPreferences().getUsertoken().isEmpty())
                    showPopup();
                else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        cardViewReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyPolicy.class).putExtra("type", ApiService.RETURN_POLICY));
            }
        });
        cardViewPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyPolicy.class).putExtra("type",ApiService.PRIVACY));
            }
        });
        cardViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrivacyPolicy.class).putExtra("type", ApiService.TNC));
            }
        });
        return view;
    }

    private void generateShareLink() {
        ShareAppBottomSheet bottomSheet = new ShareAppBottomSheet();
        bottomSheet.show(getActivity().getSupportFragmentManager(), "ShareBS");
    }

    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.Companion.getDatabase(getActivity().getApplicationContext()).notificationDao().deleteAllNotifications();
            }
        });
        startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("from", "logout"));
    }

    private void sendToGmail(String subject, String email) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (SPData.getAppPreferences().getUsertoken().isEmpty()) {
            textView1.setText("Login");
            textView2.setText("Unlock more features by login");
            cardViewProfile.setVisibility(View.GONE);
            cardViewWallet.setVisibility(View.GONE);
        } else {
            textView1.setText("Logout");
            textView2.setText("Done for the day? Logout!");
            cardViewProfile.setVisibility(View.VISIBLE);
            cardViewWallet.setVisibility(View.VISIBLE);
        }
    }
}
