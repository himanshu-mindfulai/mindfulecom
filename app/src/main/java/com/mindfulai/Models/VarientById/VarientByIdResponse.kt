package com.mindfulai.Models.VarientById

import com.google.gson.annotations.SerializedName
import com.mindfulai.Models.varientsByCategory.Datum
import com.mindfulai.Models.varientsByCategory.Varient

data class VarientByIdResponse(
        @SerializedName("status")
        val status: Int? = null,
        @SerializedName("data")
        val data: Datum? = null,
        @SerializedName("errors")
        val errors: Boolean? = null,
        @SerializedName("message")
        val message: String? = null
)