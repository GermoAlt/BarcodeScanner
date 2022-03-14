package com.assa.barcodereader.activity.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.VO.ProductGroupVO
import com.assa.barcodereader.activity.main.weight_list.GroupedProductsAdapter
import com.assa.barcodereader.entity.Product
import com.assa.barcodereader.activity.main.weight_list.ProductAdapter
import com.assa.barcodereader.database.ScanDatabase
import com.assa.barcodereader.task.LoadExistingScansTask
import com.assa.barcodereader.task.ProductTask
import com.assa.barcodereader.task.UpdateScansTask
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.math.RoundingMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private const val RC_PERMISSION = 10

class MainActivity : AppCompatActivity() {

    private var mPermissionGranted = false
    private lateinit var codeScanner: CodeScanner
    private val bottomSheetView by lazy { findViewById<ConstraintLayout>(R.id.bottomSheetScanTray) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var products: ArrayList<Product> = arrayListOf()
    private var groupedProducts: ArrayList<ProductGroupVO> = arrayListOf()
    private val executor: ExecutorService = Executors.newCachedThreadPool()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val previousScans = executor.submit(LoadExistingScansTask(applicationContext)).get()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetView.setOnClickListener { toggleBottomSheetVisibility() }

        val scannedListRecyclerView = findViewById<RecyclerView>(R.id.product_list)
        val groupedListRecyclerView = findViewById<RecyclerView>(R.id.grouped_recycler_view)

        val groupedProductsAdapter = GroupedProductsAdapter(this, groupedProducts)
        val groupedProductsLayoutManager = LinearLayoutManager(this)
        groupedListRecyclerView.adapter = groupedProductsAdapter
        groupedListRecyclerView.layoutManager = groupedProductsLayoutManager
        groupedListRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

        val productLayoutManager = LinearLayoutManager(this)
        val productAdapter = ProductAdapter(products) {
            groupedProductsAdapter.handleProductDeletion(it)
            updateTotalAmounts()
        }
        scannedListRecyclerView.adapter = productAdapter
        scannedListRecyclerView.layoutManager = productLayoutManager
        scannedListRecyclerView.addItemDecoration(DividerItemDecoration(applicationContext, LinearLayoutManager.VERTICAL))

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        configScanner(scannerView)

        previousScans.forEach { product -> processNewScan(product, scannedListRecyclerView, groupedListRecyclerView) }

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (products.find { p -> p.barcodeNumber == it.text } == null) {
                    val product = executor.submit(ProductTask(it.text, applicationContext)).get()
                    if (product != null) {
                        processNewScan(product, scannedListRecyclerView, groupedListRecyclerView)
                    }
                }
            }
        }

        findViewById<Button>(R.id.button_delete_all_scans).setOnClickListener {
            MaterialAlertDialogBuilder(this, R.style.Theme_BarcodeReader_Dialog)
                .setTitle(resources.getString(R.string.delete_all_scans_title))
                .setMessage(resources.getString(R.string.delete_all_scans_message))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                    products.clear()
                    groupedProducts.clear()
                    updateTotalAmounts()
                    productAdapter.notifyDataSetChanged()
                    groupedProductsAdapter.notifyDataSetChanged()
                }
                .show()
        }
    }

    private fun processNewScan(product: Product, scannedListRecyclerView: RecyclerView, groupedListRecyclerView: RecyclerView){
        product.weight = product.weight.setScale(2, RoundingMode.DOWN)
        products.add(0, product)
        scannedListRecyclerView.adapter?.notifyItemInserted(0)
        scannedListRecyclerView.layoutManager?.scrollToPosition(0)

        updateTotalAmounts()

        val group: ProductGroupVO? =
            groupedProducts.find { pg -> pg.description == product.description }
        if (group != null) {
            group.products.add(0,product)
            groupedListRecyclerView.adapter?.notifyItemChanged(groupedProducts.indexOf(group))
        } else {
            groupedProducts.add(0,ProductGroupVO(product))
            groupedListRecyclerView.adapter?.notifyItemInserted(0)
            groupedListRecyclerView.layoutManager?.scrollToPosition(0)
        }
    }

    private fun updateTotalAmounts() {
        val weightTotal = findViewById<TextView>(R.id.text_total_weight)
        val elementTotal = findViewById<TextView>(R.id.text_total_element)

        elementTotal.text = (products.size).toString()
        weightTotal.text = resources.getString(R.string.weight_total, products.sumOf { p -> p.weight }.setScale(2, RoundingMode.DOWN))
        Executors.newCachedThreadPool().submit(UpdateScansTask(applicationContext,products))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true
                codeScanner.startPreview()
            } else {
                mPermissionGranted = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun configScanner(scannerView: CodeScannerView){
        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat, ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Error de camara: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mPermissionGranted = false
            requestPermissions(listOf(Manifest.permission.CAMERA).toTypedArray(), RC_PERMISSION)
        } else {
            mPermissionGranted = true
        }
    }

    private fun toggleBottomSheetVisibility() {
        bottomSheetBehavior.state =
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
    }
}