package com.mindfulai.otp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class AutoDetectOTP {
    private SmsCallback smsCallback;
    private Context context;
    private BroadcastReceiver chargerReceiver;

    public AutoDetectOTP(Context context) {
        this.context = context;
    }

    public void startSmsRetriver(final SmsCallback smsCallback) {
        registerReceiver();
        this.smsCallback = smsCallback;
        SmsRetrieverClient client = SmsRetriever.getClient(context);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                smsCallback.connectionSuccess(aVoid);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                smsCallback.connectionfailed("" + e);
            }
        });

    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);

        chargerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    assert extras != null;
                    Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                    assert status != null;
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            smsCallback.smsCallback(message);
                            stopSmsReciever();
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            smsCallback.connectionfailed("Time out");
                            break;

                    }
                }
            }
        };
        context.registerReceiver(chargerReceiver, intentFilter);
    }

    private void stopSmsReciever() {
        try {
            context.unregisterReceiver(chargerReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public interface SmsCallback {
        void connectionfailed(String e);

        void connectionSuccess(Void aVoid);

        void smsCallback(String sms);
    }
    public static String getHashCode(Context context) {
        AppSignatureHelper appSignature = new AppSignatureHelper(context);
        Log.e(" getAppSignatures ", "" + appSignature.getAppSignatures());
        return appSignature.getAppSignatures().get(0);
    }
}