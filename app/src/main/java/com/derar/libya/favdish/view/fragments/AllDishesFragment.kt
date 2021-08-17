package com.derar.libya.favdish.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.derar.libya.favdish.R
import com.derar.libya.favdish.application.FavDishApplication
import com.derar.libya.favdish.databinding.FragmentAllDishesBinding
import com.derar.libya.favdish.view.activities.AddUpdateDishActivity
import com.derar.libya.favdish.viewmodel.FavDishViewModel
import com.derar.libya.favdish.viewmodel.FavDishViewModelFactory
import com.derar.libya.favdish.viewmodel.home.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder


class AllDishesFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentAllDishesBinding? = null

    private val mFavDishViewModel:FavDishViewModel by viewModels{
        FavDishViewModelFactory(
            (requireActivity().application as FavDishApplication).repository
        )
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner, Observer {dishes->
            dishes?.let {dishesList->
                var result:StringBuilder = StringBuilder()
                dishesList.forEach { favDishItem->
                    result.append("Dish Title: ${favDishItem.id} :: ${favDishItem.title} \n")
                }
                Snackbar.make(view,result.toString(),Snackbar.LENGTH_LONG).show()
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
        _binding = null
    }
}