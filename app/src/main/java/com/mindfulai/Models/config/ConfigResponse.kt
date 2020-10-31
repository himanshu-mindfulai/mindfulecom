package com.mindfulai.Models.config

import com.google.gson.annotations.SerializedName

data class ConfigResponse(
        @SerializedName("status")
        var status: Long? = null,
        @SerializedName("data")
        var data: ConfigModel? = null,
        @SerializedName("errors")
        var errors: Boolean? = null,
        @SerializedName("message")
        var message: String? = null
)