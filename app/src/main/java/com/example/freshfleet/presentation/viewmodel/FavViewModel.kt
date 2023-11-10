package com.example.freshfleet.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.local.FavoriteItem
import com.example.freshfleet.data.repository.FavRepository
import com.example.freshfleet.domain.model.FavProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavViewModel(private val favRepository: FavRepository) : ViewModel() {

    private val _favItems = MutableLiveData<MutableList<FavProduct>>()
    val favItems: LiveData<MutableList<FavProduct>> = _favItems

    private var favProductList: MutableList<FavProduct> = mutableListOf()

    init {

        CoroutineScope(Dispatchers.IO).launch {

            favRepository.getAllFavoriteItems().collect { favoriteItems ->
                favProductList = favoriteItems.map { favoriteItem ->
                    FavProduct(
                        favoriteItem.image,
                        favoriteItem.itemId,
                        favoriteItem.name,
                        favoriteItem.price,
                        favoriteItem.unit,
                        favoriteItem.inCart
                    )
                }.toMutableList()

                withContext(Dispatchers.Main) {
                    _favItems.value = favProductList
                }
            }
        }
    }
}
