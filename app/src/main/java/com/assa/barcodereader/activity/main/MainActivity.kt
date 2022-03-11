package com.assa.barcodereader.activity.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.VO.ProductGroupVO
import com.assa.barcodereader.activity.main.weight_list.GroupedProductsAdapter
import com.assa.barcodereader.entity.Product
import com.assa.barcodereader.activity.main.weight_list.ProductAdapter
import com.assa.barcodereader.task.ProductTask
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.math.RoundingMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private const val RC_PERMISSION = 10

class MainActivity : AppCompatActivity() {

    private val mCodeScanner: CodeScanner? = null
    private var mPermissionGranted = false
    private lateinit var codeScanner: CodeScanner
    private val bottomSheetView by lazy { findViewById<ConstraintLayout>(R.id.bottomSheetScanTray) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)

        val scannedList = findViewById<RecyclerView>(R.id.product_list)
        val products : ArrayList<Product> = arrayListOf()
        val productAdapter = ProductAdapter(this, products)
        val productLayoutManager = LinearLayoutManager(this)
        scannedList.adapter = productAdapter
        scannedList.layoutManager = productLayoutManager

        val groupedList = findViewById<RecyclerView>(R.id.grouped_recycler_view)
        val groupedProducts: ArrayList<ProductGroupVO> = arrayListOf()
        val groupedProductsAdapter = GroupedProductsAdapter(this, groupedProducts)
        val groupedProductsLayoutManager = LinearLayoutManager(this)
        groupedList.adapter = groupedProductsAdapter
        groupedList.layoutManager = groupedProductsLayoutManager

        var lastScanned = ""
        val executor: ExecutorService = Executors.newCachedThreadPool()
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        configScanner(scannerView)

        bottomSheetView.setOnClickListener { toggleBottomSheetVisibility()}

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (!it.text.equals(lastScanned)
                    && products.find { p -> p.barcodeNumber == it.text } == null) {
                    val product = executor.submit(ProductTask(it.text, applicationContext)).get()
                    if (product != null) {
                        product.weight = product.weight.setScale(2, RoundingMode.DOWN)
                        updateTotalAmounts(products)
                        products.add(0, product)
                        productAdapter.notifyItemInserted(0)
                        productLayoutManager.scrollToPosition(0)
                        val group: ProductGroupVO? = groupedProducts.find { pg -> pg.description == product.description }
                        if (group != null){
                            group.products.add(product)
                            groupedProductsAdapter.notifyItemChanged(groupedProducts.indexOf(group))
                        } else {
                            groupedProducts.add(ProductGroupVO(product))
                            groupedProductsAdapter.notifyItemInserted(0)
                            groupedProductsLayoutManager.scrollToPosition(0)
                        }
                        lastScanned = it.text
                    }
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
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

    private fun updateTotalAmounts(products: ArrayList<Product>) {
        val weightTotal = findViewById<TextView>(R.id.text_total_weight)
        val elementTotal = findViewById<TextView>(R.id.text_total_element)

        elementTotal.text = (products.size).toString()
        weightTotal.text = resources.getString(R.string.weight_total, products.sumOf { p -> p.weight })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true
                mCodeScanner!!.startPreview()
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
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
    }

    private fun toggleBottomSheetVisibility() {
        bottomSheetBehavior.state =
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED)
                BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
    }

}