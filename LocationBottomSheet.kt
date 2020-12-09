package com.mindfulai.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindfulai.Activites.MainActivity
import com.mindfulai.Activites.MapActivity
import com.mindfulai.ministore.R
import java.lang.ClassCastException

class LocationBottomSheet: BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.location_bs_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.bt_locatino_on).setOnClickListener {
            context?.startActivity(Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
            ))
        }

        view.findViewById<TextView>(R.id.manual_location).setOnClickListener {
            try{
                (context as (MainActivity)).openSelectAddressActivity()
            } catch (e: ClassCastException){
                (context as (MapActivity)).entermanually()
            }
        }
    }
}