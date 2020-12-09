package com.mindfulai.ui

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindfulai.Adapter.NotificationAdapter
import com.mindfulai.dao.AppDatabase
import com.mindfulai.ministore.R

class NotificationFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var rv = view.findViewById<RecyclerView>(R.id.rv_notifications)
        var no = view.findViewById<LinearLayout>(R.id.no_item_layout)

        AsyncTask.execute {
            var list = AppDatabase.getDatabase(requireContext())?.notificationDao()?.getAllNotifications()
            if (list?.isNotEmpty()!!){
                no.visibility = View.GONE
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = context?.let { NotificationAdapter(it, list) }
            }
        }

    }
}