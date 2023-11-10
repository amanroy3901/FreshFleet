package com.example.freshfleet.presentation.ui.fragments

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshfleet.R
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.local.CartItem
import com.example.freshfleet.data.local.FavoriteItem
import com.example.freshfleet.data.repository.FavRepository
import com.example.freshfleet.data.repository.ShopCartRepository
import com.example.freshfleet.databinding.FragmentHomeBinding
import com.example.freshfleet.domain.model.Category
import com.example.freshfleet.domain.model.Item
import com.example.freshfleet.presentation.ui.adapter.CategoriesAdapter
import com.example.freshfleet.presentation.ui.adapter.ItemClickListener
import com.example.freshfleet.presentation.viewmodel.CartViewModelFactory
import com.example.freshfleet.presentation.viewmodel.CategoryType
import com.example.freshfleet.presentation.viewmodel.ProductMainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryBottomSheetDialog: BottomSheetDialog
    private lateinit var database: AppDatabase
    private lateinit var cartRepository: ShopCartRepository
    private lateinit var favRepository: FavRepository
    private lateinit var categoryAdapter: CategoriesAdapter
    private lateinit var viewmodel: ProductMainViewModel
    private val categoryList: ArrayList<Category> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initializeUI(inflater,container)
        initializeData()
        return binding.root
    }
//
//    private fun initializeData() {
//        categoryList.clear()
//        database = AppDatabase.getDatabase(requireContext())
//        cartRepository = ShopCartRepository(database)
//        favRepository = FavRepository(database)
//
//        viewmodel = ViewModelProvider(
//            this,
//            CartViewModelFactory(cartRepository, favRepository, database, requireContext())
//        ).get(ProductMainViewModel::class.java)
//        viewmodel.initializeDataCall()
//
//        // Use CategoriesAdapter and set it to RecyclerView
//        categoryAdapter = CategoriesAdapter(requireContext(), categoryList, this)
//        binding.productWithCategory.adapter = categoryAdapter
//
//        viewmodel.filteredCategoryList.observe(viewLifecycleOwner) {
//            categoryList.clear()
//            categoryList.addAll(it)
//            // Notify the adapter about the data change
//            categoryAdapter.updateData(categoryList)
//        }
//    }

    private fun initializeData() {
        database = AppDatabase.getDatabase(requireContext())
        cartRepository = ShopCartRepository(database)
        favRepository = FavRepository(database)

        viewmodel = ViewModelProvider(
            this,
            CartViewModelFactory(cartRepository, favRepository, database, requireContext())
        ).get(ProductMainViewModel::class.java)

        // Initialize adapter and set it to the RecyclerView
        categoryAdapter = CategoriesAdapter(requireContext(), arrayListOf(), this)
        binding.productWithCategory.adapter = categoryAdapter

        // Observer for filteredCategoryList
        viewmodel.filteredCategoryList.observe(viewLifecycleOwner) { newList ->
            updateCategoryList(newList)
        }
    }

    private fun updateCategoryList(newList: List<Category>) {
        // Using DiffUtil to efficiently update the list
        val diffCallback = CategoriesAdapter.CategoriesDiffCallback(categoryAdapter.parentItems, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Update the data in the adapter
        categoryAdapter.updateData(newList)

        // Dispatch updates to the adapter
        diffResult.dispatchUpdatesTo(categoryAdapter)
    }


            private fun initializeUI(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        showToolbar()
        binding.productWithCategory.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryFilter.setOnClickListener { showCategoryBottomSheetDialog() }
    }

    private fun showCategoryBottomSheetDialog() {
        val categoryBottomSheetView: View =
            LayoutInflater.from(requireContext()).inflate(R.layout.bottom_filter_category, null)

        categoryBottomSheetDialog = BottomSheetDialog(requireContext())
        categoryBottomSheetDialog.setContentView(categoryBottomSheetView)
        val closeButton = categoryBottomSheetView.findViewById<View>(R.id.closeIcon)

        setCategoryClickListeners(categoryBottomSheetView)
        setSelectedCategory(viewmodel.selectedID)

        closeButton.setOnClickListener { categoryBottomSheetDialog.dismiss() }
        categoryBottomSheetDialog.show()
    }

    private fun setCategoryClickListeners(view: View) {
        val categoryIds = arrayOf(
            R.id.optionFood,
            R.id.optionBeverages,
            R.id.optionPooja,
            R.id.optionElectronic,
            R.id.optionHygiene,
            R.id.optionAll
        )

        categoryIds.forEach { id ->
            view.findViewById<TextView>(id).setOnClickListener {
                viewmodel.applyCategoryOnList(id)
                setSelectedCategory(id)
                viewmodel.setSelectedId(id)
                categoryBottomSheetDialog.dismiss()
            }
        }
    }

    private fun setSelectedCategory(selectedId: Int?) {
        selectedId?.let {
            val textView = categoryBottomSheetDialog.findViewById<TextView>(it)
            textView?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.canary_yellow)
            )
        }
    }

    private fun showToolbar() {
        val activity: Activity? = activity
        if (activity != null) {
            val toolbar: Toolbar = activity.findViewById(R.id.toolbar)
            toolbar.visibility = View.VISIBLE
        }
    }

    override fun onAddButtonClick(item: Item, position: Int, parentPosition: Int) {
        if (!item.isInCart) {
            CoroutineScope(Dispatchers.IO).launch {
                database.cartDao().upsertCartItem(
                    CartItem(
                        item.icon,
                        item.id,
                        item.name,
                        item.price,
                        1
                    )
                )
            }
            if(item.isFavourite) {
                CoroutineScope(Dispatchers.IO).launch {
                    database.favoriteDao().upsertFavItem(
                        FavoriteItem(
                            item.icon,
                            item.id,
                            item.name,
                            item.price,
                            1,
                            true
                        )
                    )
                    viewmodel.updatetheList()
                }
            }
            categoryAdapter.notifyItemChanged(parentPosition)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                database.cartDao().delete(
                    CartItem(
                        item.icon,
                        item.id,
                        item.name,
                        item.price,
                        1
                    )
                )
            }
            if(item.isFavourite) {
                CoroutineScope(Dispatchers.IO).launch {
                    database.favoriteDao().upsertFavItem(
                        FavoriteItem(
                            item.icon,
                            item.id,
                            item.name,
                            item.price,
                            1,
                            false
                        )
                    )
                    viewmodel.updatetheList()
                }
            }
            categoryAdapter.notifyItemChanged(parentPosition)
        }
    }

    override fun onHeartButtonClick(item: Item) {
        if (!item.isFavourite) {
            CoroutineScope(Dispatchers.IO).launch {
                database.favoriteDao().upsertFavItem(
                    FavoriteItem(
                        item.icon,
                        item.id,
                        item.name,
                        item.price,
                        1,
                        item.isInCart
                    )
                )
                viewmodel.updatetheList()
            }
            categoryAdapter.notifyDataSetChanged()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                database.favoriteDao().deleteFavItem(
                    FavoriteItem(
                        item.icon,
                        item.id,
                        item.name,
                        item.price,
                        1,
                        item.isInCart
                    )
                )
                viewmodel.updatetheList()
            }
            categoryAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        viewmodel.resetData()
    }
}
