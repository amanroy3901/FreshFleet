package com.example.freshfleet.presentation.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshfleet.R
import com.example.freshfleet.databinding.CategoryListBinding
import com.example.freshfleet.domain.model.Category
import com.example.freshfleet.domain.model.Item

class CategoriesAdapter(
    val context: Context,
    var parentItems: ArrayList<Category>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    lateinit var itemsAdapter: ItemsAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CategoryListBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.category_list, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productCategory = parentItems[position]
        holder.binding.categoryController.text = productCategory.name
        holder.binding.productCart.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemsAdapter = ItemsAdapter(context, productCategory.items, position, itemClickListener)
        holder.binding.productCart.adapter = itemsAdapter
        if (productCategory.hideItems) {
            holder.binding.productCart.visibility = View.GONE
            holder.binding.categoryController.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_expanded,0)

        } else {
            holder.binding.categoryController.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_chevron_right,0)
            holder.binding.productCart.visibility = View.VISIBLE
        }

        holder.binding.categoryController.setOnClickListener {
            productCategory.hideItems = !productCategory.hideItems
            notifyItemChanged(holder.adapterPosition, productCategory) // Pass the changed item to DiffUtil
        }
    }

    override fun getItemCount(): Int {
        return parentItems.size
    }

    override fun getItemId(position: Int): Long {
        return parentItems[position].id.toLong()
    }

    // Implement DiffUtil callback
    class CategoriesDiffCallback(
        private val oldList: List<Category>,
        private val newList: List<Category>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

    }

    // Function to update data with DiffUtil
    fun updateData(newData: List<Category>) {
        val diffCallback = CategoriesDiffCallback(parentItems, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        parentItems.clear()
        parentItems.addAll(newData)
        notifyDataSetChanged()
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(val binding: CategoryListBinding) : RecyclerView.ViewHolder(binding.root)
}
