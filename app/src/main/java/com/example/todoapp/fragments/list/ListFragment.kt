package com.example.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoVieModel
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.fragments.SharedViewModel
import com.example.todoapp.fragments.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val mToDoViewModel: ToDoVieModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    private val adapter: ListAdapter by lazy { ListAdapter() }

    private lateinit var listAdapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // DataBinding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setup RecyclerView
        setupRecylerView()

        //observe livedata
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
            //binding.recyclerView.scheduleLayoutAnimation()
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_delete_all -> confirmRemoval()
                    R.id.menu_priority_high ->
                        mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner) {
                            adapter.setData(it)
                        }

                    R.id.menu_priority_low ->
                        mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner) {
                            adapter.setData(it)
                        }

                    android.R.id.home -> requireActivity().onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecylerView() {
        val recyclerView = binding.rvListTodo
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = LandingAnimator().apply {
            addDuration = 310
        }


        //swipe to delete
        swipeToDelete(recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]

                //deleted item
                mToDoViewModel.deleteItem(deletedItem)

                //restore deleted item
                restoreDeletedData(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData, position: Int) {
        val snackbar = Snackbar.make(
            view, "Deleted  '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackbar.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    private fun searchThroughDatabase(query: String?) {
        val searchQuery = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

    //confirm to remove all item from database
    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(
                requireContext(),
                "Succsecfully Removed Everything!",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete Everything?")
        builder.setMessage("Are you sure you want remove this Everything")
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

//without data binding
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.list_fragment_menu, menu)
//
//        val search = menu.findItem(R.id.menu_search)
//        val searchView = search.actionView as? SearchView
//        searchView?.isSubmitButtonEnabled = true
//        searchView?.setOnQueryTextListener(this)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.menu_delete_all) {
//            confirmRemoval()
//        }
//        return super.onOptionsItemSelected(item)
//    }