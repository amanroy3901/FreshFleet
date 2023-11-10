package com.example.freshfleet.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshfleet.R
import com.example.freshfleet.databinding.ProductCardBinding
import com.example.freshfleet.domain.model.Item

class ItemsAdapter(
    val context: Context,
    private var childItems: ArrayList<Item>,
    val parentPosition: Int,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ProductCardBinding>(
            LayoutInflater.from(parent.context), R.layout.product_card, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productCard = childItems[position]
        holder.binding.productName.text = productCard.name
        Glide.with(context).load(productCard.icon).centerInside().into(holder.binding.productImage)
        holder.binding.productCost.text = productCard.price.toString()
        // Add button click listeners
        // Set the icon dynamically based on some condition or data
        if (productCard.isFavourite) {
            holder.binding.favouriteButton.setImageResource(R.drawable.icon_favourite_fill)
        } else {
            holder.binding.favouriteButton.setImageResource(R.drawable.icon_favorite)
        }

        if (productCard.isInCart) {
            holder.binding.addButton.setImageResource(R.drawable.shopping_cart)
        } else {
            holder.binding.addButton.setImageResource(R.drawable.icon_add)
        }

        holder.binding.addButton.setOnClickListener {
            itemClickListener.onAddButtonClick(productCard,position,parentPosition)
//            productCard.isInCart = true
//            notifyItemChanged(holder.adapterPosition)
        }

        holder.binding.favouriteButton.setOnClickListener {
            itemClickListener.onHeartButtonClick(productCard)
        }
    }

    override fun getItemCount(): Int {
        return childItems.size

    }

    class ViewHolder(val binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root)

}

interface ItemClickListener {
    fun onAddButtonClick(item: Item,position: Int,parentPosition: Int)
    fun onHeartButtonClick(item: Item)
}