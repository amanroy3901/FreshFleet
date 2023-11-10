package com.example.freshfleet.domain.model

data class Category(
    val id: Int,
    val items: ArrayList<Item>,
    val name: String,
    var hideItems:Boolean = false
)