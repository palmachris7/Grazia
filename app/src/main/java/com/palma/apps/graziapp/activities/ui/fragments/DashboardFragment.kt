package com.palma.apps.graziapp.activities.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.DetalleProductoActivity
import com.palma.apps.graziapp.activities.SettingsActivity
import com.palma.apps.graziapp.activities.ui.adapters.DashboardItemsListAdapter
import com.palma.apps.graziapp.databinding.FragmentDashboardBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.Constantes

class DashboardFragment : BaseFragment() {

//    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null


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
//        dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_cuenta ->{
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
          /*  R.id.action_cart->{
                startActivity(Intent(activity,CartListActivity::class.java))
                return true
            }*/
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

//        binding.shimmerViewContainer.startShimmerAnimation()

        getDashboardItemsList()
    }


    fun successDashboardItemList(dashboardItemsList : ArrayList<Prenda>){
        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()
//        hideProgressDialog()

//        for(i in dashboardItemsList){
//            Log.i("Item Title ::", i.title)
//        }

        if (dashboardItemsList.size > 0 ){
            binding.rvMyProductsItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

            val adapter = DashboardItemsListAdapter(requireActivity(),dashboardItemsList)
            binding.rvMyProductsItems.layoutManager = GridLayoutManager(activity,2)
            binding.rvMyProductsItems.setHasFixedSize(true)
            binding.rvMyProductsItems.adapter = adapter

            adapter.setOnClickListener(object : DashboardItemsListAdapter.OnClickListener{
                override fun onClick(position: Int, prenda: Prenda) {
                    val intent = Intent(requireActivity(),DetalleProductoActivity::class.java)
                    intent.putExtra(Constantes.EXTRA_PRODUCT_ID,prenda.product_id)
                    intent.putExtra(Constantes.EXTRA_PRODUCT_OWNER_ID,prenda.user_id)
                    startActivity(intent)
                }

            })

        }else{
            binding.rvMyProductsItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }

    }
    private fun getDashboardItemsList(){
//        showProgressDialog(getString(R.string.txtEspere))

        binding.shimmerViewContainer.startShimmerAnimation()

        FireStoreClass().getDashboardItemsList(this)
    }
}