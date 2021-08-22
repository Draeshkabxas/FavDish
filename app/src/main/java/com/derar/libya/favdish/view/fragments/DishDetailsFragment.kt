package com.derar.libya.favdish.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.derar.libya.favdish.R
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.FragmentDishDetailsBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.utils.Constants
import com.derar.libya.favdish.viewmodel.FavDishViewModel
import com.derar.libya.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException


class DishDetailsFragment : Fragment() {

    //  Step 7: Create a ViewBinding variable.
    // START
    private var mBinding: FragmentDishDetailsBinding? = null
    // END

    private var mFavDishDetails : FavDish? = null

    private val mFavDishViewModel : FavDishViewModel by viewModels {
      FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        /** set menu */
        setHasOptionsMenu(true)

        super.onCreate(savedInstanceState)
    }


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

        mFavDishDetails = args.dishDetails
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
            mBinding!!.tvCookingDirection.text =
                getStringFromHtml(dish.dishDetails.directionToCook)
            mBinding!!.tvCookingTime.text =
                resources.getString(
                    R.string.lbl_estimate_cooking_time,
                    dish.dishDetails.cookingTime
                )
        }
        // END

        if (args.dishDetails.favoriteDish) {
            mBinding!!.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_selected
                )
            )
        } else {
            mBinding!!.ivFavoriteDish.setImageDrawable(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.ic_favorite_unselected
                )
            )

        }



        mBinding!!.ivFavoriteDish.setOnClickListener {

            // Step 6: Update the favorite dish variable based on the current selection. i.e If it true then make it false vice-versa.
            // START
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish
            // END

            // Step 7: Pass the updated values to ViewModel
            // START
            mFavDishViewModel.update(args.dishDetails)
            // END

            // Step 8: Update the icons and display the toast message accordingly.
            // START
            if (args.dishDetails.favoriteDish) {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )

                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_unselected
                    )
                )

                Toast.makeText(
                    requireActivity(),
                    resources.getString(R.string.msg_removed_from_favorite),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share_dish -> {
                val type = "text/plain"
                val subject = "Check this dish recipe"
                var extraText = ""
                val shareWith = "Share With"

                mFavDishDetails?.let {
                    var image = ""
                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                        image = it.image
                    }

                    val cookingInstructions =getStringFromHtml(it.directionToCook)

                    extraText =
                        "$image \n" +
                                "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                                "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstructions" +
                                "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }


                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

            }
        }
        return true
    }


    /**
     * This function gets a string from passed html text
     * @param htmlText the html text that will be convert to string
     */
    fun getStringFromHtml(htmlText:String):String=
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             Html.fromHtml(
                htmlText,
                Html.FROM_HTML_MODE_COMPACT
            ).toString()
        } else {
            @Suppress("DEPRECATION")
             Html.fromHtml(
                 htmlText
            ).toString()
        }

    //  Step 9: Override the onDestroy function to make the mBinding null that is avoid the memory leaks. This we have not done before because the AllDishesFragment because when in it the onDestroy function is called the app is killed. But this is the good practice to do it.
    // START
    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
    // END

}
