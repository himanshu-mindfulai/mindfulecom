package com.mindfulai.Activites

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindfulai.Adapter.SelectAddressAdapter
import com.mindfulai.Adapter.UserAddressesAdapter
import com.mindfulai.Models.UserBaseAddress
import com.mindfulai.Models.UserDataAddress
import com.mindfulai.NetworkRetrofit.ApiUtils
import com.mindfulai.Utils.CommonUtils
import com.mindfulai.Utils.SPData
import com.mindfulai.ministore.databinding.ActivityAddressSelectorBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressSelectorActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddressSelectorBinding
    lateinit var addressesList: List<UserDataAddress>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Select a address"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        addressesList = ArrayList();
        binding.rvAddresses.layoutManager = LinearLayoutManager(this)
        binding.btAddAddress.setOnClickListener {
            startActivity(Intent(
                    this, AddAddressActivity::class.java
            ).putExtra("title", "Add Address"))
        }
        getAddresses()
    }

    override fun onResume() {
        super.onResume()
        getAddresses()
    }

    private fun getAddresses() {
        var customProgressDialog = CommonUtils.showProgressDialog(baseContext, "Getting addresses...")
        customProgressDialog.show()
        var apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().usertoken)
        apiService.userBaseAddress.enqueue(object: Callback<UserBaseAddress>{
            override fun onFailure(call: Call<UserBaseAddress>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<UserBaseAddress>, response: Response<UserBaseAddress>) {
                if (response.isSuccessful){
                    CommonUtils.hideProgressDialog(customProgressDialog)
                    addressesList = response.body()?.data!!
                    if(addressesList.isEmpty()){
                        binding.noItemLayout.visibility = View.VISIBLE
                    } else {
                        var selectAddressAdapter = SelectAddressAdapter(baseContext, addressesList, this@AddressSelectorActivity)
                        binding.rvAddresses.adapter = selectAddressAdapter
                    }
                }
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}