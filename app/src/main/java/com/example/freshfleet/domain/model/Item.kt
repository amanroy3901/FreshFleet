package com.example.freshfleet.domain.model

data class Item(
    val icon: String,
    val id: Int,
    val name: String,
    val price: Double,
    var isFavourite: Boolean = false,
    var isInCart: Boolean = false
)