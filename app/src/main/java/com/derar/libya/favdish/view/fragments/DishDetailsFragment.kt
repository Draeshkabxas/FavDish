package com.derar.libya.favdish.view.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.derar.libya.favdish.R
import com.derar.libya.favdish.databinding.FragmentDishDetailsBinding
import java.io.IOException


class DishDetailsFragment : Fragment() {

    //  Step 7: Create a ViewBinding variable.
    // START
    private var mBinding: FragmentDishDetailsBinding? = null
    // END

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Step 8: Initialize the mBinding variable.
        // START
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding!!.root
        // END
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        // Step 10: Remove the log and populate the data to UI.
        // START
        args.let { dish ->

            try {
                // Load the dish image in the ImageView.
                Glide.with(requireActivity())
                    .load(dish.dishDetails.image)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Tag", "Error Loading an Image.")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            resource?.let {
                                // Step 3: Generate the Palette and set the vibrantSwatch as the background of the view.
                                // START
                                Palette.from(resource.toBitmap())
                                    .generate { palette ->
                                        val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                        mBinding!!.rlDishDetailMain.setBackgroundColor(intColor)
                                    }
                            }
                                // END
                            return false
                        }
                    })
                    .into(mBinding!!.ivDishImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mBinding!!.tvTitle.text = dish.dishDetails.title
            mBinding!!.tvType.text =
                dish.dishDetails.type.replaceFirstChar { firstChar ->
                    firstChar.uppercase()
                } // Used to make first letter capital
            mBinding!!.tvCategory.text = dish.dishDetails.category
            mBinding!!.tvIngredients.text = dish.dishDetails.ingredients
            mBinding!!.tvCookingDirection.text = dish.dishDetails.directionToCook
            mBinding!!.tvCookingTime.text =
                resources.getString(
                    R.string.lbl_estimate_cooking_time,
                    dish.dishDetails.cookingTime
                )
        }
        // END
    }

    //  Step 9: Override the onDestroy function to make the mBinding null that is avoid the memory leaks. This we have not done before because the AllDishesFragment because when in it the onDestroy function is called the app is killed. But this is the good practice to do it.
    // START
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
    // END

}
