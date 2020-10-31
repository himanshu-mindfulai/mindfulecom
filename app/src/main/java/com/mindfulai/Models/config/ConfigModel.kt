package com.mindfulai.Models.config

import com.google.gson.annotations.SerializedName

data class ConfigModel(
        @SerializedName("deliveryCharges")
        var deliveryCharges: DeliveryChargesModel? = null,
        var carryBagPrice: Float
)