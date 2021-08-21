package com.derar.libya.favdish.view.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.derar.libya.favdish.R
import com.derar.libya.favdish.databinding.ItemDishLayoutBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.utils.Constants.EXTRA_DISH_DETAILS
import com.derar.libya.favdish.view.activities.AddUpdateDishActivity
import com.derar.libya.favdish.view.fragments.AllDishesFragment
import com.derar.libya.favdish.view.fragments.FavoriteDishesFragment

class FavDishAdapter(
    private val fragment: Fragment
): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes:List<FavDish> = listOf()


    class ViewHolder(
        mBinding:ItemDishLayoutBinding
    ):RecyclerView.ViewHolder(mBinding.root) {
        val ivDishImage=mBinding.ivDishImage
        val tvDishTitle=mBinding.tvDishTitle
        val ibMore =mBinding.ibMore
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
        //  Step 7: We want the menu icon should be visible only in the AllDishesFragment not in the FavoriteDishesFragment so add the below to achieve it.
        // START
        if (fragment is AllDishesFragment) {
            holder.ibMore.visibility = View.VISIBLE
        } else if (fragment is FavoriteDishesFragment) {
            holder.ibMore.visibility = View.GONE
        }
        // END

        //  Step 6: Assign the click event to the ib_more icon and Popup the menu items.
        // START
        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, it)
            //Inflating the Popup using xml file
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

            //  Step 8: Assign the click event to the menu items as below and print the Log or You can display the Toast message for now.
            // START
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit_dish -> {
                        val intent = Intent(fragment.requireActivity(),AddUpdateDishActivity::class.java)
                        intent.putExtra(EXTRA_DISH_DETAILS,dish)
                        fragment.requireActivity().startActivity(intent)

                    }
                    R.id.action_delete_dish -> {
                        Log.i("You have clicked on", "Delete Option of ${dish.title}")
                    }
                }
                true
            }
            // END

            popup.show() //showing popup menu
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