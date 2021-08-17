package com.derar.libya.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.derar.libya.favdish.R
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.FragmentAllDishesBinding
import com.derar.libya.favdish.view.activities.AddUpdateDishActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** Set recycle view layout manager to be grid layout */
        mBinding?.rvDishesList?.layoutManager = GridLayoutManager(requireActivity(),2)

        /** set recycle view adapter to be fav dish adapter */
        val favDishAdapter= FavDishAdapter(this@AllDishesFragment)
        mBinding?.rvDishesList?.adapter = favDishAdapter

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
            dishes.let { dishesList->
                var result:String  = " "
                if (dishesList.isNotEmpty()) {
                    mBinding?.rvDishesList?.visibility = View.VISIBLE
                    mBinding?.tvNoDishesAddedYet?.visibility = View.GONE
                    result+= dishesList[0]
                    favDishAdapter.dishesList(dishesList)
                } else{
                    mBinding?.rvDishesList?.visibility = View.GONE
                    mBinding?.tvNoDishesAddedYet?.visibility = View.VISIBLE
                    result+="No Data"
                }
                Toast.makeText(requireContext(), result ,Toast.LENGTH_LONG).show()
            }

        })
    }


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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}