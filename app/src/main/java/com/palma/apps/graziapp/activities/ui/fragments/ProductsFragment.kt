package com.palma.apps.graziapp.activities.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.AddProductActivity
import com.palma.apps.graziapp.activities.ui.adapters.ListaProductosAdapter
import com.palma.apps.graziapp.databinding.FragmentProductsBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Prenda
import com.palma.apps.graziapp.utils.Constantes


class ProductsFragment : BaseFragment() {

//    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentProductsBinding? = null

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
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun deleteProduct(productID : String){

        showAlertDialogToDeleteProduct(productID)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_products,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        getProductListFromFirestore()
        super.onResume()
    }

    private fun getProductListFromFirestore(){

//        showProgressDialog(getString(R.string.txtEspere))

        binding.shimmerViewContainer.startShimmerAnimation()

        FireStoreClass().getProductList(this)
    }
    fun successProductListFromFirestore(prendaList : ArrayList<Prenda>){

//        hideProgressDialog()
            binding.shimmerViewContainer.visibility = View.GONE
            binding.shimmerViewContainer.stopShimmerAnimation()
        if (prendaList.size > 0){

            binding.rvMyProductsItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.rvMyProductsItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyProductsItems.setHasFixedSize(true)
            val adapter = ListaProductosAdapter(requireContext(),prendaList,this)
            binding.rvMyProductsItems.adapter = adapter
        }else{
            binding.rvMyProductsItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }
    fun productDeleteSuccess(){
        hideProgressDialog()

        Toast.makeText(requireActivity(), getString(R.string.productoeliminado), Toast.LENGTH_SHORT).show()

        getProductListFromFirestore()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showAlertDialogToDeleteProduct(productID : String){
        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(getString(R.string.eliminar))
        builder.setMessage(getString(R.string.preguntaeliminar))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(getString(R.string.si)){
            dialogInterface,_->

            showProgressDialog(getString(R.string.txtEspere))

            FireStoreClass().deleteProduct(this,productID)

            deleteonDatabase("prenda prueba")
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)){
            dialogInterface,_->
            dialogInterface.dismiss()
        }

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun deleteonDatabase(productName : String){
        val  rooturl = Constantes.URL
        val url= rooturl+"eliminarP.php"
        val queue: RequestQueue = Volley.newRequestQueue(requireActivity().applicationContext)
        var resultadoPost = object : StringRequest(Request.Method.POST,url,
            Response.Listener { response ->
               Toast.makeText(requireActivity().applicationContext,"El produco se eliminó de la base de datos",Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(requireActivity().applicationContext,"Error al elminar el producto $error",Toast.LENGTH_LONG).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parametros=HashMap<String,String>()
                parametros.put("nombre",productName)
                return parametros
            }
        }
        queue.add(resultadoPost)
    }


}