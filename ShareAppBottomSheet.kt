package com.mindfulai.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.mindfulai.Models.CustomerInfo.CustomerData
import com.mindfulai.NetworkRetrofit.ApiUtils
import com.mindfulai.Utils.CommonUtils
import com.mindfulai.Utils.CustomProgressDialog
import com.mindfulai.Utils.SPData
import com.mindfulai.ministore.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShareAppBottomSheet: BottomSheetDialogFragment() {
    lateinit var tvCode: TextView
    lateinit var tvCopy: TextView
    lateinit var btShare: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.share_app_bs_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvCode = view.findViewById(R.id.code)
        tvCopy = view.findViewById(R.id.copy)
        btShare = view.findViewById(R.id.share)
        tvCopy.visibility = View.INVISIBLE
        btShare.visibility = View.INVISIBLE
        tvCode.text = "Generating unique code..."
        var apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().usertoken)
        apiService.profileDetails.enqueue(object: Callback<CustomerData>{
            override fun onFailure(call: Call<CustomerData>, t: Throwable) {
                tvCode.text = "Couldn't generate code!"
                t.printStackTrace()
            }

            override fun onResponse(call: Call<CustomerData>, response: Response<CustomerData>) {
                if (response.isSuccessful){
                    val invitationLink = "https://mindfulecom.in/?invitedby=${response.body()?.data?.user?.referralCode}"
                    FirebaseDynamicLinks
                            .getInstance()
                            .createDynamicLink()
                            .setLink(Uri.parse(invitationLink))
                            .setDomainUriPrefix("https://iamsuperstore.page.link")
                            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                            .buildShortDynamicLink()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    var shortLink = task.result?.shortLink
                                    tvCode.text = shortLink.toString()
                                    tvCopy.visibility = View.VISIBLE
                                    btShare.visibility = View.VISIBLE
                                    tvCopy.setOnClickListener {
                                        var clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        var data = ClipData.newPlainText(
                                                "Share app",
                                                "${SPData.getAppShareText()} ${tvCode.text}")
                                        clipboardManager.setPrimaryClip(data)
                                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                    btShare.setOnClickListener {
                                        context?.startActivity(
                                                Intent.createChooser(
                                                    Intent(Intent.ACTION_SEND)
                                                            .setType("text/plain")
                                                            .putExtra(Intent.EXTRA_SUBJECT, "Iamsuperstore")
                                                            .putExtra(Intent.EXTRA_TEXT, "${SPData.getAppShareText()} ${tvCode.text}"),
                                                        "Share"
                                                )
                                        )
                                    }
                                } else {
                                    Log.i("ShareAppBS", task.exception?.printStackTrace().toString())
                                }
                            }
                }
            }
        })
    }
}