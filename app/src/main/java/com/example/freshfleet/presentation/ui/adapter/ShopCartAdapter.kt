package com.example.freshfleet.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshfleet.R
import com.example.freshfleet.databinding.ShopCartCardBinding
import com.example.freshfleet.domain.model.ShopCartItem

class ShopCartAdapter(val context: Context, val shopCartItems: List<ShopCartItem>, val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ShopCartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ShopCartCardBinding>(
            LayoutInflater.from(parent.context),
            R.layout.shop_cart_card,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productCard = shopCartItems[position]
        holder.binding.cartProductName.text = productCard.name
        Glide.with(context)
            .load(productCard.image)
            .centerInside()
            .into(holder.binding.cartProductImage)
        holder.binding.cartProductUnit.text = "${productCard.unit}"
        holder.binding.cartProductCost.text = "${productCard.price}/unit"
        holder.binding.cartProductTotalCost.text = String.format("%.2f",productCard.price * productCard.unit)
        holder.binding.cartProductAddBtn.setOnClickListener {
            productCard.unit = productCard.unit + 1
            onItemClickListener.onItemQuantityChanged(position, productCard.unit)
        }

        holder.binding.cartProductMinusBtn.setOnClickListener {
            productCard.unit = productCard.unit - 1
            onItemClickListener.onItemQuantityChanged(position, productCard.unit)
        }
    }

    override fun getItemCount(): Int {
        return shopCartItems.size

    }
    class ViewHolder(val binding: ShopCartCardBinding) : RecyclerView.ViewHolder(binding.root)

}

interface OnItemClickListener {
    fun onItemQuantityChanged(position: Int, newQuantity: Int)
}