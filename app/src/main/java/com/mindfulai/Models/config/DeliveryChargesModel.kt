package com.mindfulai.Models.config

import com.google.gson.annotations.SerializedName

data class DeliveryChargesModel(
        @SerializedName("belowValueCharge")
        var belowValueCharge: Long? = null,
        @SerializedName("orderValue")
        var orderValue: Long? = null,
        @SerializedName("aboveOrSameValueCharge")
        var aboveOrSameValueCharge: Long? = null
)