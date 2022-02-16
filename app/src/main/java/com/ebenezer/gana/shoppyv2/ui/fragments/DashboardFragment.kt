package com.ebenezer.gana.shoppyv2.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.FragmentDashboardBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.Products
import com.ebenezer.gana.shoppyv2.ui.activities.CartListActivity
import com.ebenezer.gana.shoppyv2.ui.activities.SettingsActivity
import com.ebenezer.gana.shoppyv2.ui.adapters.DashboardListAdapter


class DashboardFragment : BaseFragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return binding?.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {

                startActivity(Intent(activity, SettingsActivity::class.java))

                return true
            }
            R.id.action_cart -> {

                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }


        return super.onOptionsItemSelected(item)

    }


    fun successDashboardItemsList(dashboardItemList: ArrayList<Products>) {
        hideProgressDialog()
        /* for (item in dashboardItemList){
             Log.i("Item Title", item.title)
         }*/

        binding?.let {
            if (dashboardItemList.size > 0) {
                binding!!.rvDashboardItems.visibility = View.VISIBLE
                binding!!.tvNoDashboardItemsFound.visibility = View.GONE

                // spanCount is set to 2 after every 5th item
                val layoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                layoutManager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return if((position + 1) % 5 == 0) 2 else 1
                    }
                }
                binding!!.rvDashboardItems.layoutManager = layoutManager

                binding!!.rvDashboardItems.setHasFixedSize(true)
                val allProductsAdapter = DashboardListAdapter(requireActivity(), dashboardItemList)
                binding!!.rvDashboardItems.adapter = allProductsAdapter

            } else {
                binding!!.rvDashboardItems.visibility = View.GONE

                binding!!.tvNoDashboardItemsFound.visibility = View.VISIBLE
            }
        }




    }

    private fun getDashboardItemList() {
        //show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}