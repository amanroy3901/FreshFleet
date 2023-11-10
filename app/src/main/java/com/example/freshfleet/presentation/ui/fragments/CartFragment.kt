package com.example.freshfleet.presentation.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract.Data
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
import com.example.freshfleet.data.repository.FavRepository
import com.example.freshfleet.data.repository.ShopCartRepository
import com.example.freshfleet.databinding.FragmentCartBinding
import com.example.freshfleet.domain.model.ShopCartItem
import com.example.freshfleet.presentation.ui.adapter.OnItemClickListener
import com.example.freshfleet.presentation.ui.adapter.ShopCartAdapter
import com.example.freshfleet.presentation.viewmodel.CartViewModelFactory
import com.example.freshfleet.presentation.viewmodel.ShopCartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartFragment : Fragment(), OnItemClickListener {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var adapter: ShopCartAdapter
    private lateinit var database: AppDatabase
    private lateinit var cartRepository: ShopCartRepository
    private lateinit var favRepository: FavRepository
    private lateinit var shopcartProductList: ArrayList<ShopCartItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupData()
        hideToolbar()
        initializeUI()
        return fragmentCartBinding.root
    }

    private fun setupData() {
        shopcartProductList = ArrayList()
        database = AppDatabase.getDatabase(requireContext())
        cartRepository = ShopCartRepository(database)
        favRepository = FavRepository(database)
    }

    private fun hideToolbar() {
        val activity: Activity? = activity
        if (activity != null) {
            val toolbar: Toolbar = activity.findViewById(R.id.toolbar)
            toolbar.visibility = View.GONE
        }
    }

    private fun initializeUI() {
        fragmentCartBinding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_cart, null, false
        )

        fragmentCartBinding.productCardList.layoutManager = LinearLayoutManager(requireContext())

        initViewModel()
        configureUIComponents()
    }

    private fun initViewModel() {
        val viewmodel = ViewModelProvider(
            this,
            CartViewModelFactory(cartRepository, favRepository, database, null)
        ).get(ShopCartViewModel::class.java)

        viewmodel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            shopcartProductList.clear()
            shopcartProductList.addAll(cartItems)

            if (shopcartProductList.isNotEmpty()) {
                updateTotalCostContainer(shopcartProductList)
                updateRecyclerView(shopcartProductList)
                updateItemsVisibility(DataStatus.HAVE_DATA)
            } else {
                updateItemsVisibility(DataStatus.HAVE_NODATA)
            }
        }
    }

    private fun configureUIComponents() {
        configureToolbar()
        fragmentCartBinding.checkoutBtn.setOnClickListener {
            Toast.makeText(requireContext(), "checkout button clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureToolbar() {
        fragmentCartBinding.cartToolbar.setNavigationIcon(R.drawable.icon_arrow_back)
        fragmentCartBinding.cartToolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateRecyclerView(shopcartProductList: ArrayList<ShopCartItem>) {
        adapter = ShopCartAdapter(requireContext(), shopcartProductList, this)
        fragmentCartBinding.productCardList.adapter = adapter
    }

    override fun onItemQuantityChanged(position: Int, newQuantity: Int) {
        val product = adapter.shopCartItems[position]
        product.unit = newQuantity
        adapter.notifyItemChanged(position)
        updateTotalCostContainer(adapter.shopCartItems)
        if (newQuantity > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                database.cartDao().upsertCartItem(
                    CartItem(
                        product.image,
                        product.id,
                        product.name,
                        product.price,
                        product.unit
                    )
                )
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                database.cartDao().delete(
                    CartItem(
                        product.image,
                        product.id,
                        product.name,
                        product.price,
                        product.unit
                    )
                )
            }
        }
    }

    private fun updateTotalCostContainer(shopCartItems: List<ShopCartItem>) {
        val price = shopCartItems.sumOf { item -> item.price * item.unit }
        fragmentCartBinding.shopCartSubTotalCost.text = String.format("%.2f", price)
        fragmentCartBinding.shopCartTotalCost.text = String.format("%.2f", price)
    }

    private fun updateItemsVisibility(dataStatus: DataStatus) {
        val hasData = dataStatus == DataStatus.HAVE_DATA
        val visibility = if (hasData) View.VISIBLE else View.GONE
        fragmentCartBinding.noDataText.visibility = if (hasData) View.GONE else View.VISIBLE
        fragmentCartBinding.shopCartCostCalculatorContainer.visibility = visibility
        fragmentCartBinding.checkoutBtn.visibility = visibility
    }
}

enum class DataStatus {
    HAVE_DATA,
    HAVE_NODATA
}
