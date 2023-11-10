package com.example.freshfleet.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.repository.ShopCartRepository
import com.example.freshfleet.domain.model.ShopCartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopCartViewModel(val cartRepository: ShopCartRepository) : ViewModel() {

    private val _cartItems = MutableLiveData<MutableList<ShopCartItem>>()
    val cartItems: LiveData<MutableList<ShopCartItem>> = _cartItems

    private var shopcartProductList: MutableList<ShopCartItem> = mutableListOf()

    init {

        CoroutineScope(Dispatchers.IO).launch {

            cartRepository.getAllCartItems().collect { shopCartItems ->
                shopcartProductList = shopCartItems.map { item ->
                    ShopCartItem(
                        item.image,
                        item.itemId,
                        item.name,
                        item.price,
                        item.unit
                    )
                }.toMutableList()
                withContext(Dispatchers.Main) {
                    _cartItems.value = shopcartProductList
                }

            }

        }
    }

}