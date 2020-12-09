package com.mindfulai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mindfulai.ministore.R

class PriceDetailsBottomSheet(var total: String, var deliveryFee: String,var promotion: String,var carybag: String,var cartTotal: String) : BottomSheetDialogFragment() {

    lateinit var totalAmountTV: TextView
    lateinit var deliveryFeeTV: TextView
    lateinit var promotionTV: TextView
    lateinit var carybagPrice: TextView
    lateinit var cartTotalTv: TextView
    lateinit var close: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.price_details_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalAmountTV = view.findViewById(R.id.total)
        deliveryFeeTV = view.findViewById(R.id.delivery_fee)
        promotionTV = view.findViewById(R.id.coupon_discount)
        cartTotalTv = view.findViewById(R.id.cart_total)
        carybagPrice = view.findViewById(R.id.carybag_price)
        close = view.findViewById(R.id.close)


        totalAmountTV.text = context?.resources?.getString(R.string.rs) + total
        if (cartTotal.isNullOrEmpty()){
            cartTotalTv.text = context?.resources?.getString(R.string.rs) + total
        } else {
            cartTotalTv.text = context?.resources?.getString(R.string.rs) + cartTotal
        }
        if (deliveryFee.isNullOrEmpty()){
            deliveryFeeTV.text = "Free"
        } else {
            deliveryFeeTV.text ="+ "+ context?.resources?.getString(R.string.rs) + deliveryFee+"0"
            deliveryFeeTV.setTextColor(resources.getColor(R.color.colorError))
        }
        if(carybag.isNullOrEmpty()){
            view.findViewById<RelativeLayout>(R.id.carybag_applied).visibility = View.GONE
        }else{
            carybagPrice.text = "+ "+getString(R.string.rs)+carybag
            carybagPrice.setTextColor(resources.getColor(R.color.colorError))
        }
        if (promotion.isNullOrEmpty()){
            view.findViewById<RelativeLayout>(R.id.promotion_applied).visibility = View.GONE
        } else {
            promotionTV.text = "- "+context?.resources?.getString(R.string.rs) + promotion
            promotionTV.setTextColor(resources.getColor(R.color.colorGreen))
        }
        close.setOnClickListener {
            dismiss()
        }
    }
}