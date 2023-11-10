package com.example.freshfleet.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshfleet.R
import com.example.freshfleet.databinding.CategoryListBinding
import com.example.freshfleet.databinding.FavCardBinding
import com.example.freshfleet.domain.model.Category
import com.example.freshfleet.domain.model.FavProduct

class FavAdapter(val context: Context, var favItem: List<FavProduct>, val onfavItemClickListener: OnfavItemClickListener) :
    RecyclerView.Adapter<FavAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FavCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.fav_card, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favProduct = favItem[position]
        holder.binding.favProductName.text = favProduct.name
        Glide.with(context)
            .load(favProduct.image)
            .centerInside()
            .into(holder.binding.favProductImage)
        holder.binding.favProductUnit.text = "${favProduct.unit} Unit"
        holder.binding.favProductCost.text = favProduct.price.toString()
        holder.binding.favIcon.setOnClickListener {
            onfavItemClickListener.onHeartClickListener(position)
        }
        holder.binding.favProductAddBtn.setOnClickListener {
            onfavItemClickListener.onAddClickListener(position,favProduct)
        }
        if (favProduct.inCart) {
            holder.binding.favProductAddBtn.setImageResource(R.drawable.shopping_cart)
        } else {
            holder.binding.favProductAddBtn.setImageResource(R.drawable.icon_add)
        }
    }

    override fun getItemCount(): Int {
        return favItem.size
    }

    class ViewHolder(val binding: FavCardBinding) : RecyclerView.ViewHolder(binding.root)
}

interface OnfavItemClickListener {
    fun onHeartClickListener(position: Int)
    fun onAddClickListener(position: Int, favcProduct: FavProduct)
}
