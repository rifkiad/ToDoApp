package com.example.todoapp.fragments.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoVieModel
import com.example.todoapp.databinding.FragmentAddBinding
import com.example.todoapp.fragments.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class AddFragment : Fragment() {

    private val mToDoViewModel: ToDoVieModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()


    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)


        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_add) {
                    insertDataToDb()
                } else if (menuItem.itemId == android.R.id.home) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun insertDataToDb() {
        val mTitle = binding.titleEdt.text.toString()
        val mPriority = binding.prioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)
        if (validation) {

            //Insert Data to Database
            val newData = ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Snackbar.make(requireView(), "Successfully added!", Snackbar.LENGTH_SHORT).show()
            // navigation back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Snackbar.make(requireView(), "Please fill out all fields", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}

//            Toast.makeText(requireContext(), "Succesfully added!", Toast.LENGTH_SHORT).show()
//
//            //navigation back
//            findNavController().navigate(R.id.action_addFragment_to_listFragment)
//        } else {
//            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT)
//                .show()
//        }