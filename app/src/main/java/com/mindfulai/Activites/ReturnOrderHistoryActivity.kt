package com.mindfulai.Activites

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mindfulai.Adapter.OrderHistoryDetailsAdapter
import com.mindfulai.Models.AllOrderHistory.Datum
import com.mindfulai.Models.AllOrderHistory.DatumModel
import com.mindfulai.Models.AllOrderHistory.OrderHistory
import com.mindfulai.Models.AllOrderHistory.Product
import com.mindfulai.NetworkRetrofit.ApiUtils
import com.mindfulai.Utils.CommonUtils
import com.mindfulai.Utils.CustomProgressDialog
import com.mindfulai.Utils.SPData
import com.mindfulai.ministore.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReturnOrderHistoryActivity : AppCompatActivity() {

    private lateinit var btReturn: Button
    private lateinit var rvProducts: RecyclerView
    private lateinit var tvSelectItem: TextView
    private lateinit var productsList: ArrayList<Product>
    private var returnList = arrayListOf<String>()
    private var id: String = ""
    private var actionreturn = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_order_history)

        actionreturn = intent.getStringExtra("return_action")
        id = intent.getStringExtra("id")

        productsList = intent.getParcelableArrayListExtra("products_List")
        tvSelectItem = findViewById(R.id.tv_select_items)
        rvProducts = findViewById(R.id.rv_products)
        btReturn = findViewById(R.id.bt_return_order)

        tvSelectItem.text = "Please select items you want to " + actionreturn
        btReturn.text = actionreturn
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)
        rvProducts.layoutManager = layoutManager

        var list = productsList
        var iterator = list.iterator()
        while (iterator.hasNext()){
            var i = iterator.next()
            if(i.status !=null &&(i.status == "Returned" || i.status == "Replaced" || !i.returnable)){
                iterator.remove()
            }
        }


        rvProducts.adapter = OrderHistoryDetailsAdapter(this,list,"action","")

        btReturn.setOnClickListener{
            Log.i("TAAAAG","products id $$$$$$$$$$$$$$$: "+ returnList)
            returnOrder()
        }
    }

    private fun returnOrder() {
        var customProgressDialog = CustomProgressDialog(this, "Loading... please wait")
        customProgressDialog.show()
        val apiService = ApiUtils.getHeaderAPIService(SPData.getAppPreferences().usertoken)
        val jsonObject = JsonObject()
        jsonObject.addProperty("action", actionreturn)
        jsonObject.addProperty("order", id)
        var products =  JsonArray()
        for (string in returnList){
            products.add(string)
        }
        jsonObject.add("products", products)
        apiService.orderAction(jsonObject).enqueue(object : Callback<DatumModel>{
            override fun onFailure(call: Call<DatumModel>, t: Throwable) {
                Log.e("returnOrder", t.message)
                t.printStackTrace()
            }

            override fun onResponse(call: Call<DatumModel>, response: Response<DatumModel>) {
                if(response.isSuccessful){
                    CommonUtils.hideProgressDialog(customProgressDialog)
                    var alertDialogBuilder = AlertDialog.Builder(this@ReturnOrderHistoryActivity)
                    var view = layoutInflater.inflate(R.layout.sucess_dialog_layout, null)
                    alertDialogBuilder.setView(view)
                    var alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                    var displayMetrics = DisplayMetrics()
                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                    var layoutParams = WindowManager.LayoutParams();
                    layoutParams.copyFrom(alertDialog.window?.attributes)
                    layoutParams.width = (displayMetrics.widthPixels * 0.7f).toInt()
                    layoutParams.height = (displayMetrics.heightPixels * 0.3f).toInt()
                    alertDialog.window?.attributes = layoutParams
                    var timer = object: CountDownTimer(5000, 1000){
                        override fun onFinish() {
                            var orderHisotryData = response.body()?.data
                            val i = Intent(baseContext, OrderHistoryDetailsActivity::class.java)
                            i.putParcelableArrayListExtra("orderHistoryData", orderHisotryData?.products as java.util.ArrayList<out Parcelable?>)
                            i.putExtra("order_id_", orderHisotryData?.id)
                            i.putExtra("order_id", "# " + orderHisotryData?.orderId)
                            val date: String? = orderHisotryData?.orderDate
                            val date1 = date?.split("T".toRegex())?.toTypedArray()
                            //date1[0]: 2020-06-18
                            //date1[0]: 2020-06-18
                            val date2 = date1?.get(0)?.split("-".toRegex())?.toTypedArray()
                            val strDate = (date2?.get(1) ?: "") + "-" + (date2?.get(2) ?: "") + "-" + (date2?.get(0) ?: "")
                            i.putExtra("order_date", strDate)
                            i.putExtra("order_amount", resources.getString(R.string.rs) + orderHisotryData?.amount.toString())
                            i.putExtra("order_address", orderHisotryData?.address)
                            i.putExtra("order_delivery_slot", orderHisotryData?.deliverySlot)
                            i.putExtra("order_payment_method", orderHisotryData?.paymentMethod)
                            alertDialog.dismiss()
                            finish()
                            startActivity(i)
                        }
                        override fun onTick(millisUntilFinished: Long) { }
                    }.start()
                }
            }

        })
    }

    fun add(id: String) {
        returnList.add(id)
    }
    fun remove(id: String) {
        returnList.remove(id)
    }
}
