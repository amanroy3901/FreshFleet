package com.example.freshfleet.domain.model

data class productCategory(
    val categories: List<Category>,
    val error: Any,
    val message: String,
    val status: Boolean
)