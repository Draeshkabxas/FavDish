package com.derar.libya.favdish.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.derar.libya.favdish.databinding.ItemDishLayoutBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.view.fragments.AllDishesFragment
import com.derar.libya.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(
    private val fragment: Fragment
): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes:List<FavDish> = listOf()


    class ViewHolder(
        private val mBinding:ItemDishLayoutBinding
    ):RecyclerView.ViewHolder(mBinding.root) {
        val ivDishImage=mBinding.ivDishImage
        val tvDishTitle=mBinding.tvDishTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:ItemDishLayoutBinding = ItemDishLayoutBinding.inflate(
            LayoutInflater.from(fragment.context),parent,false
        )

        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val dish = dishes[position]
        // Set the ivDishImage to be dish image
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        // Set tvDishTitle the to be dish title
        holder.tvDishTitle.text = dish.title
        holder.itemView.setOnClickListener{
            when (fragment) {
                is AllDishesFragment -> {
                    fragment.dishDetails(dish)
                }
                is FavoriteDishesFragment -> {
                    fragment.dishDetails(dish)
                }
            }
        }

    }

    override fun getItemCount(): Int =dishes.size


    /**
     * Change dishes list in the adapter to be passed list
     * and notify the adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    fun dishesList(list:List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }


}