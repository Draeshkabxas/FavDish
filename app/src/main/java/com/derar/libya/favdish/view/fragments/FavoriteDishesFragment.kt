package com.derar.libya.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.FragmentFavoriteDishesBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.view.activities.MainActivity
import com.derar.libya.favdish.view.adapters.FavDishAdapter
import com.derar.libya.favdish.viewmodel.FavDishViewModel
import com.derar.libya.favdish.viewmodel.FavDishViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder

class FavoriteDishesFragment : Fragment() {

    private var _binding: FragmentFavoriteDishesBinding? = null

   private val mFavDishViewModel:FavDishViewModel by viewModels{
       FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
   }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFavDishViewModel.favoriteDishesList.observe(viewLifecycleOwner) { dishes ->

            dishes.let {

                //Step 5: Remove the Logs and display the list of Favorite Dishes using RecyclerView. Here we will not create a separate adapter class we cas use the same that we have created for AllDishes.

                // START

                // Set the LayoutManager that this RecyclerView will use.
                binding.rvFavoriteDishesList.layoutManager =
                    GridLayoutManager(requireActivity(), 2)
                // Adapter class is initialized and list is passed in the param.
                val adapter = FavDishAdapter(this@FavoriteDishesFragment)
                // adapter instance is set to the recyclerview to inflate the items.
                binding.rvFavoriteDishesList.adapter = adapter

                if (it.isNotEmpty()) {
                    binding.rvFavoriteDishesList.visibility = View.VISIBLE
                    binding.tvNoFavoriteDishesAvailable.visibility = View.GONE

                    adapter.dishesList(it)
                } else {
                    binding.rvFavoriteDishesList.visibility = View.GONE
                    binding.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                }
                // END
            }
        }

    }


    /**
     * A function to navigate to the Dish Details Fragment.
     */
    fun dishDetails(favDish: FavDish){

        // TODO Step 9: Call the hideBottomNavigationView function when user wants to navigate to the DishDetailsFragment.
        // START
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
        // END

        findNavController()
            .navigate(FavoriteDishesFragmentDirections.actionNavigationFavoriteDishesToDishDetailsFragment(
                favDish
            ))
    }

    //  Step 10: Override the onResume method and call the function to show the BottomNavigationView when user is on the AllDishesFragment.
    // START
    override fun onResume() {
        super.onResume()

        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }
    // END

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}