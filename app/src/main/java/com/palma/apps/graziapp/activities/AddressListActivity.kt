package com.palma.apps.graziapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.palma.apps.graziapp.R
import com.palma.apps.graziapp.activities.ui.adapters.DireccionLAdapter
import com.palma.apps.graziapp.databinding.ActivityAddressListBinding
import com.palma.apps.graziapp.firestore.FireStoreClass
import com.palma.apps.graziapp.modelos.Direccion
import com.palma.apps.graziapp.utils.Constantes
import com.palma.apps.graziapp.utils.SwipeToDeleteCallback
import com.palma.apps.graziapp.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private var mSelectAddress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()
        @Suppress("DEPRECATION")
        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(intent,Constantes.ADD_ADDRESS_REQUEST_CODE)
        }

        if (intent.hasExtra(Constantes.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constantes.EXTRA_SELECT_ADDRESS, false)
        }
        getAddressList()


    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constantes.ADD_ADDRESS_REQUEST_CODE){
            getAddressList()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)

        binding.toolbarAddressListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUpAddressInUI(direccionList: ArrayList<Direccion>) {
//        hideProgressDialog()

        binding.shimmerViewContainer.visibility = View.GONE
        binding.shimmerViewContainer.stopShimmerAnimation()

        if (mSelectAddress) {
            binding.tvTitle.text = getString(R.string.selecdireccion)
            if (direccionList.size > 0) {
                binding.tvAddAddress.visibility = View.GONE
            }else{
                binding.tvAddAddress.visibility = View.VISIBLE
            }

        }

        if (direccionList.size > 0) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE

            binding.rvAddressList.layoutManager = LinearLayoutManager(this)
            binding.rvAddressList.setHasFixedSize(true)
            val adapter = DireccionLAdapter(this, direccionList,mSelectAddress)
            binding.rvAddressList.adapter = adapter

            if (!mSelectAddress) {

                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapters = binding.rvAddressList.adapter as DireccionLAdapter
                        adapters.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        showProgressDialog(getString(R.string.txtEspere))
                        FireStoreClass().deleteAddress(
                            this@AddressListActivity,
                            direccionList[viewHolder.adapterPosition].id
                        )

                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
            }
        } else {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE
        }
    }

   private fun getAddressList(){
//       showProgressDialog(getString(R.string.txtEspere))

       binding.shimmerViewContainer.startShimmerAnimation()

       FireStoreClass().getAddressList(this)
   }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddressListActivity,
            getString(R.string.errordirec),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()

    }
}