package com.derar.libya.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.derar.libya.favdish.R
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.FragmentRandomDishBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.model.entities.RandomDish
import com.derar.libya.favdish.utils.Constants
import com.derar.libya.favdish.utils.Constants.DISH_IMAGE_SOURCE_ONLINE
import com.derar.libya.favdish.viewmodel.FavDishViewModel
import com.derar.libya.favdish.viewmodel.FavDishViewModelFactory
import com.derar.libya.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {


    private var _binding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentRandomDishBinding.inflate(inflater, container, false)


        return binding.root
    }

    fun showCustomProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }
    fun hideCustomProgressDialog(){
        mProgressDialog?.dismiss()
    }
    //  Step 2: Override the onViewCreated method and Initialize the ViewModel variable.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ViewModel variable.
        mRandomDishViewModel =
            ViewModelProvider(this).get(RandomDishViewModel::class.java)

        // Step 3: Call the function to get the response from API.
        // START
        mRandomDishViewModel.getRandomRecipeFromAPI()
        // END

        // Step 5: Call the observer function.
        // START
        randomDishViewModelObserver()
        // END

        binding.srlRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromAPI()
        }
    }

    //  Step 4: Create a function to get the data in the observer after the API is triggered.
    // START
    /**
     * A function to get the data in the observer after the API is triggered.
     */
    private fun randomDishViewModelObserver() {

        mRandomDishViewModel.randomDishRespond.observe(
            viewLifecycleOwner,
            Observer { randomDishResponse ->
                randomDishResponse?.let {

                    /** make swipereFreshLayout stop refreshing */
                    if(binding.srlRandomDish.isRefreshing)
                        binding.srlRandomDish.isRefreshing = false



                    setRandomDishResponseInUI(randomDishResponse.recipes[0])
                    Log.i("Random Dish Response", "${randomDishResponse.recipes[0]}")
                }
            })

        mRandomDishViewModel.randomDishLoadingError.observe(
            viewLifecycleOwner,
            Observer { dataError ->
                dataError?.let {
                    /** make swipereFreshLayout stop refreshing */
                    if(binding.srlRandomDish.isRefreshing)
                        binding.srlRandomDish.isRefreshing = false



                    Log.i("Random Dish API Error", "$dataError")
                }
            })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, Observer { loadRandomDish ->
            loadRandomDish?.let {

                if (loadRandomDish && !binding.srlRandomDish.isRefreshing) {
                    /** show progress dialog */
                    showCustomProgressDialog()
                }else{
                    /** hide progress dialog */
                    hideCustomProgressDialog()
                }

                Log.i("Random Dish Loading", "$loadRandomDish")
            }
        })
    }
    // END


    // Step 1: Create a method to populate the API response in the UI.
    // START
    /**
     * A method to populate the API response in the UI.
     *
     * @param recipe - Data model class of the API response with filled data.
     */
    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {

        // Load the dish image in the ImageView.
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding.ivDishImage)

        binding.tvTitle.text = recipe.title

        // Default Dish Type
        var dishType: String = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            binding.tvType.text = dishType
        }

        // There is not category params present in the response so we will define it as Other.
        binding.tvCategory.text = "Other"

        val ingredients =StringBuilder()
        for (value in recipe.extendedIngredients) {

            if (ingredients.isEmpty()) {
                ingredients.append(value.original)
            } else {
                ingredients.append(", \n ${value.original}")
            }
        }

        binding.tvIngredients.text = ingredients.toString()

        // The instruction or you can say the Cooking direction text is in the HTML format so we will you the fromHtml to populate it in the TextView.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            binding.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }



        binding.tvCookingTime.text =
            resources.getString(
                R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString()
            )

        // step 6: Assign the click event to the Favorite Button and add the dish details to the local database if user click on it.
        // START

        var addedToFavorite = false

        binding.ivFavoriteDish.setOnClickListener {

            var toastMassage =resources.getString(R.string.msg_already_added_to_favorites)

            if(!addedToFavorite) {
                // Step 7: Create a instance of FavDish data model class and fill it with required information from the API response.
                // START
                val randomDishDetails = FavDish(
                    recipe.image,
                    DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients.toString(),
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )
                // END

                // Step 8: Create an instance of FavDishViewModel class and call insert function and pass the required details.
                // START
                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                mFavDishViewModel.insert(randomDishDetails)
                // END

                addedToFavorite =true

                // Step 9: Once the dish is inserted you can acknowledge user by Toast message as below and also update the favorite image by selected.
                // START
                binding.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                toastMassage =  resources.getString(R.string.msg_added_to_favorites)
            }

            Toast.makeText(
                requireActivity(),
               toastMassage,
                Toast.LENGTH_SHORT
            ).show()
            // END
        }
        // END
    }
    // END


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}