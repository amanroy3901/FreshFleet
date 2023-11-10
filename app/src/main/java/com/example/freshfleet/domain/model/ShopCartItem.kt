package com.example.freshfleet.domain.model

data class ShopCartItem(
    val image: String, val id: Int, val name: String, val price: Double, var unit: Int
)

