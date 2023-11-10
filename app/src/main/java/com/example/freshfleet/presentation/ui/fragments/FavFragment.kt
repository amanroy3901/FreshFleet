package com.example.freshfleet.presentation.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshfleet.R
import com.example.freshfleet.data.local.AppDatabase
import com.example.freshfleet.data.local.CartItem
import com.example.freshfleet.data.local.FavoriteItem
import com.example.freshfleet.data.repository.FavRepository
import com.example.freshfleet.data.repository.ShopCartRepository
import com.example.freshfleet.databinding.FragmentFavBinding
import com.example.freshfleet.domain.model.FavProduct
import com.example.freshfleet.presentation.ui.adapter.FavAdapter
import com.example.freshfleet.presentation.ui.adapter.OnfavItemClickListener
import com.example.freshfleet.presentation.viewmodel.CartViewModelFactory
import com.example.freshfleet.presentation.viewmodel.FavViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavFragment : Fragment(), OnfavItemClickListener {

    private lateinit var fragmentFavBinding: FragmentFavBinding
    private lateinit var adapter: FavAdapter
    private lateinit var database: AppDatabase
    private lateinit var cartRepository: ShopCartRepository
    private lateinit var favRepository: FavRepository
    private lateinit var favProductList: ArrayList<FavProduct>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Initialize data structures
        favProductList = ArrayList()
        database = AppDatabase.getDatabase(requireContext())
        cartRepository = ShopCartRepository(database)
        favRepository = FavRepository(database)

        // Inflate the layout for this fragment
        fragmentFavBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_fav, container, false
        )

        // Initialize ViewModel and observe changes in fav items
        initViewModel()

        // Setup UI components
        setupUI()

        return fragmentFavBinding.root
    }

    // Initialize ViewModel
    private fun initViewModel() {
        val viewmodel = ViewModelProvider(
            this, CartViewModelFactory(cartRepository, favRepository, database, null)
        ).get(FavViewModel::class.java)

        // Observe changes in the list of favorite items
        observeFavItems(viewmodel)
    }

    // Observe changes in the list of favorite items
    private fun observeFavItems(viewmodel: FavViewModel) {
        viewmodel.favItems.observe(viewLifecycleOwner) { favItems ->
            // Clear existing list and update with new data
            favProductList.clear()
            favItems?.let {
                favProductList.addAll(favItems)

            }
            // Update RecyclerView if there are items, otherwise show a message
            if (favProductList.isNotEmpty()) {
                fragmentFavBinding.noDataText.visibility = View.GONE
                updateTheRecyclerView(favProductList)
            } else {
                updateTheRecyclerView(emptyList())
                showEmptyMessage()
            }
        }
    }

    // Setup UI components
    private fun setupUI() {
        // Hide the toolbar
        hideToolbar()

        // Configure toolbar with navigation icon and click listener
        configureToolbar()

        // Configure RecyclerView with a LinearLayoutManager
        configureRecyclerView()
    }

    // Configure toolbar with navigation icon and click listener
    private fun configureToolbar() {
        fragmentFavBinding.favToolbar.setNavigationIcon(R.drawable.icon_arrow_back)
        fragmentFavBinding.favToolbar.setNavigationOnClickListener {
            // Navigate back when the toolbar is clicked
            parentFragmentManager.popBackStack()
        }
    }

    // Configure RecyclerView with a LinearLayoutManager
    private fun configureRecyclerView() {
        fragmentFavBinding.favList.layoutManager = LinearLayoutManager(requireContext())
    }

    // Update the RecyclerView with a new list of favorite products
    private fun updateTheRecyclerView(products: List<FavProduct>) {
        adapter = FavAdapter(requireContext(), products, this)
        fragmentFavBinding.favList.adapter = adapter
    }

    // Show a message when there are no favorite items to display
    private fun showEmptyMessage() {
        fragmentFavBinding.noDataText.visibility = View.VISIBLE
    }

    // Hide the toolbar of the parent activity
    private fun hideToolbar() {
        val activity: Activity? = activity
        if (activity != null) {
            val toolbar: Toolbar = activity.findViewById(R.id.toolbar)
            toolbar.visibility = View.GONE
        }
    }

    // Handle click on the heart icon in the RecyclerView item
    override fun onHeartClickListener(position: Int) {

        val item = adapter.favItem.get(position)
        CoroutineScope(Dispatchers.IO).launch {
            database.favoriteDao().deleteFavItem(
                FavoriteItem(
                    item.image, item.id, item.name, item.price, 1, item.inCart
                )
            )
        }
    }

    // Handle click on the add icon in the RecyclerView item
    override fun onAddClickListener(position: Int, favProduct: FavProduct) {

        if (!favProduct.inCart) {

            CoroutineScope(Dispatchers.IO).launch {
                database.cartDao().upsertCartItem(
                    CartItem(
                        favProduct.image, favProduct.id, favProduct.name, favProduct.price, 1
                    )
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                database.favoriteDao().upsertFavItem(
                    FavoriteItem(
                        favProduct.image, favProduct.id, favProduct.name, favProduct.price, 1, true
                    )
                )
            }
            Toast.makeText(
                requireContext(), "${favProduct.name} added to cart", Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(
                requireContext(), "${favProduct.name} is already in cart", Toast.LENGTH_SHORT
            ).show()
        }

    }
}
