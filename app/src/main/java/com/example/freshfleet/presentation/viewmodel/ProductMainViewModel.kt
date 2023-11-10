package com.example.freshfleet.presentation.viewmodel
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshfleet.R
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.domain.model.Category
import com.example.freshfleet.domain.model.FavProduct
import com.example.freshfleet.domain.model.Item
import com.example.freshfleet.domain.model.ShopCartItem
import com.example.freshfleet.domain.model.productCategory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductMainViewModel(val database: AppDatabase, val context: Context) : ViewModel() {
    // LiveData to store filtered categories
    private val _filteredCategoryList = MutableLiveData<MutableList<Category>>()
    val filteredCategoryList: MutableLiveData<MutableList<Category>> = _filteredCategoryList

    // Lists to store favorite and cart items
    private var favProductList: MutableList<FavProduct> = mutableListOf()
    private var shopcartProductList: MutableList<ShopCartItem> = mutableListOf()

    // LiveData to temporarily save items
    private val itemSaver = MutableLiveData<MutableList<Category>>()

    // Selected category ID
    var selectedID: Int? = null

    // Initialize data call
    fun initializeDataCall() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Collect favorites and then process categories
                collectFavorites()
                // Collect cart items and then process categories
            } catch (e: Exception) {
                Log.e("ProductMainViewModel", "Error in coroutines", e)
            }
        }
    }

    // Reset data by clearing LiveData and reinitializing
    fun resetData() {
        _filteredCategoryList.value = mutableListOf() // Clear the LiveData
        initializeDataCall() // Reinitialize the data
    }

    // Update the list of items
    fun updatetheList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Collect favorites and then process categories
                collectFavorites()
                // Collect cart items and then process categories
            } catch (e: Exception) {
                Log.e("ProductMainViewModel", "Error in coroutines", e)
            }
        }
    }

    // Collect favorite items from the database
    suspend fun collectFavorites() {
        database.favoriteDao().getAllFavoriteItems().collect { favoriteItems ->
            favProductList = favoriteItems.map { favoriteItem ->
                FavProduct(
                    favoriteItem.image,
                    favoriteItem.itemId,
                    favoriteItem.name,
                    favoriteItem.price,
                    favoriteItem.unit,
                    false
                )
            }.toMutableList()
            collectCartItems()
        }
    }

    // Collect cart items from the database
    private suspend fun collectCartItems() {
        database.cartDao().getAllCartItems().collect { cartItems ->
            shopcartProductList = cartItems.map { cartItem ->
                ShopCartItem(
                    cartItem.image,
                    cartItem.itemId,
                    cartItem.name,
                    cartItem.price,
                    cartItem.unit
                )
            }.toMutableList()
            // Process categories after collecting favorites
            processCategories()
        }
    }

    // Process categories based on favorites and cart items
    private suspend fun processCategories() {
        withContext(Dispatchers.Main) {
            try {
                // Read the JSON data from the file
                val jsonInputStream = context.assets.open("shopping.json")
                val json = jsonInputStream.bufferedReader().readText()
                jsonInputStream.close()

                // Parse the JSON data into a list using Gson
                val gson = Gson()
                val productCategory = gson.fromJson(json, productCategory::class.java)
                val filteredCategories = productCategory.categories.map { category ->
                    val arrayList: ArrayList<Item> = ArrayList()
                    val updatedItems = category.items.map { item ->
                        val isItemInFavList = favProductList.any { it.id == item.id }
                        val isItemInCartList = shopcartProductList.any { it.id == item.id }

                        if (isItemInFavList) {
                            item.isFavourite = true
                        }

                        if (isItemInCartList) {
                            item.isInCart = true
                        }

                        item
                    }.toMutableList()
                    arrayList.addAll(updatedItems)
                    Category(
                        category.id,
                        arrayList,
                        category.name,
                        category.hideItems
                    )
                }
                itemSaver.value = filteredCategories.toMutableList()
                _filteredCategoryList.value = filteredCategories.toMutableList()
                selectedID?.let {
                    applyCategoryOnList(it)
                }
            } catch (e: Exception) {
                Log.e("ProductMainViewModel", "Error processing categories", e)
            }
        }
    }

    // Apply a category filter based on the selected category type
    fun applyCategoryOnList(selectedId: Int) {
        val filteredCategories = itemSaver.value?.filter { category ->
            when (selectedId) {
                R.id.optionFood -> category.name == "Food"
                R.id.optionBeverages -> category.name == "Beverages"
                R.id.optionPooja -> category.name == "Pooja Daily Needs"
                R.id.optionElectronic -> category.name == "Electronic Items"
                R.id.optionHygiene -> category.name == "Hygiene Essentials"
                R.id.optionAll -> true
                else -> false
            }
        }
        _filteredCategoryList.value = filteredCategories?.toMutableList()
    }

    // Set the selected category ID
    fun setSelectedId(id: Int) {
        selectedID = id
    }
}

// Enum to define different category types
enum class CategoryType {
    CATEGORY_ALL,
    CATEGORY_FOOD,
    CATEGORY_HYGIENE,
    CATEGORY_DRINKS,
    CATEGORY_POOJA,
    CATEGORY_ELECTRONIC
}
