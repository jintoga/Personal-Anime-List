package com.example.jintoga.animelist.repository.model.auth

import com.google.gson.annotations.SerializedName

data class ClientCredentials(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("token_type") val tokenType: String
)