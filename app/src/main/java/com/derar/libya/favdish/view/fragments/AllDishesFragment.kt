package com.derar.libya.favdish.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.derar.libya.favdish.R
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.DialogCustomListBinding
import com.derar.libya.favdish.databinding.FragmentAllDishesBinding
import com.derar.libya.favdish.model.entities.FavDish
import com.derar.libya.favdish.utils.Constants
import com.derar.libya.favdish.view.activities.AddUpdateDishActivity
import com.derar.libya.favdish.view.activities.MainActivity
import com.derar.libya.favdish.view.adapters.CustomListItemAdapter
import com.derar.libya.favdish.view.adapters.FavDishAdapter
import com.derar.libya.favdish.viewmodel.FavDishViewModel
import com.derar.libya.favdish.viewmodel.FavDishViewModelFactory
import com.derar.libya.favdish.viewmodel.home.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder


class AllDishesFragment : Fragment() {

    private var mBinding: FragmentAllDishesBinding? = null

    private val mFavDishViewModel:FavDishViewModel by viewModels{
        FavDishViewModelFactory(
            (requireActivity().application as FavDishApplication).repository
        )
    }

    private lateinit var mFavDishAdapter:FavDishAdapter
    private lateinit var mCustomListDialog:Dialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)



        return binding.root
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

    /**
     * A function to navigate to the Dish Details Fragment.
     */
    fun dishDetails(favDish: FavDish){

        // Step 9: Call the hideBottomNavigationView function when user wants to navigate to the DishDetailsFragment.
        // START
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
        // END

        findNavController()
            .navigate(AllDishesFragmentDirections.actionNavigationAllDishesToDishDetailsFragment(
                favDish
            ))
    }


    // Step 4: Create a function to show an AlertDialog while delete the dish details.
    // START
    /**
     * Method is used to show the Alert Dialog while deleting the dish details.
     *
     * @param dish - Dish details that we want to delete.
     */
    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }
    // END

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Set recycle view layout manager to be grid layout */
        mBinding?.rvDishesList?.layoutManager = GridLayoutManager(requireActivity(),2)

        /** set recycle view adapter to be fav dish adapter */
        mFavDishAdapter= FavDishAdapter(this@AllDishesFragment)
        mBinding?.rvDishesList?.adapter =mFavDishAdapter



        /**
         * When data change check if dishes list is not empty
         * if it is then set recycleView to be visible
         * and no dishes added text view to be gone
         * finally set dishes list to the adapter
         *
         * if not then set recycleView to be gone
         * and no dishes added text view to be visible
         */
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner, Observer {dishes->
            setDishesDataOrShowNoData(dishes)
        })
    }


//  Step 4: Create a function to show the filter items in the custom list dialog.
    // START
    /**
     * A function to launch the custom dialog.
     */
    private fun filterDishesListDialog() {
         mCustomListDialog= Dialog(requireActivity())

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        //  Step 5: Add the 0 element to  get ALL items.
        dishTypes.add(0, Constants.ALL_ITEMS)

        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        // Adapter class is initialized and list is passed in the param.
        val adapter = CustomListItemAdapter(
            requireActivity(),
            this@AllDishesFragment,
            dishTypes,
            Constants.FILTER_SELECTION
        )
        // adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter
        //Start the dialog and display it on screen.
        mCustomListDialog.show()
    }
    // END

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes ->{
                filterDishesListDialog()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    // Step 5: Create a function to get the filter item selection and get the list from database accordingly.
    // START
    /**
     * A function to get the filter item selection and get the list from database accordingly.
     *
     * @param filterItemSelection
     */
    fun filterSelection(filterItemSelection: String) {

        mCustomListDialog.dismiss()

        Log.i("Filter Selection", filterItemSelection)

        if (filterItemSelection == Constants.ALL_ITEMS) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                setDishesDataOrShowNoData(dishes)
            }
        } else {
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){dishes->
                setDishesDataOrShowNoData(dishes)
            }
            Log.i("Filter List", "Get Filter List")
        }
    }
    // END
    /**
     * Set function set passed dishes data to the adapter
     * but if the data is empty show no data yet text
     * @param dishes the dishes that will be set to the adapter
     */
    private fun setDishesDataOrShowNoData(dishes:List<FavDish>){
        dishes.let {
            if (it.isNotEmpty()) {

                mBinding!!.rvDishesList.visibility = View.VISIBLE
                mBinding!!.tvNoDishesAddedYet.visibility = View.GONE

                mFavDishAdapter.dishesList(it)
            } else {

                mBinding!!.rvDishesList.visibility = View.GONE
                mBinding!!.tvNoDishesAddedYet.visibility = View.VISIBLE
            }
        }
    }
   }


