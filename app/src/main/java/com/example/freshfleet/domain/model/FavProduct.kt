package com.example.freshfleet.domain.model

import android.media.Image

data class FavProduct(
    val image: String,
    val id: Int,
    val name: String,
    val price: Double,
    var unit: Int,
    var inCart: Boolean
)
