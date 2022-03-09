package com.assa.barcodereader.activity.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.entity.Product
import com.assa.barcodereader.activity.main.weight_list.ProductAdapter
import com.assa.barcodereader.task.DatabasePrepopulationTask
import com.assa.barcodereader.task.ProductTask
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private const val RC_PERMISSION = 10

class MainActivity : AppCompatActivity() {

    private val mCodeScanner: CodeScanner? = null
    private var mPermissionGranted = false
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scannedList = findViewById<RecyclerView>(R.id.product_list)
        val products : ArrayList<Product> = arrayListOf()
        val productAdapter = ProductAdapter(this, products)
        val productLayoutManager = LinearLayoutManager(this)
        scannedList.adapter = productAdapter
        scannedList.layoutManager = productLayoutManager

        var lastScanned = ""
        val executor: ExecutorService = Executors.newCachedThreadPool()
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        executor.submit(DatabasePrepopulationTask(applicationContext))
        configScanner(scannerView)


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (!it.text.equals(lastScanned)) {
                    val product = executor.submit(ProductTask(it.text, applicationContext)).get()
                    if (product != null) {
                        updateValues(product)
                        products.add(0, product)
                        productAdapter.notifyItemInserted(0)
                        productLayoutManager.scrollToPosition(0)
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

    private fun updateValues(product: Product) {
        val weightTotal = findViewById<TextView>(R.id.text_total_weight)
        val elementTotal = findViewById<TextView>(R.id.text_total_element)

        elementTotal.text = (elementTotal.text.toString().toInt() + 1).toString()
        weightTotal.text = resources.getString(R.string.weight_total,
            (weightTotal.text.toString()
            .substring(0, weightTotal.text.length - 3)
                .toBigDecimal()
                .plus(product.weight.toBigDecimal()))
            .toString())
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

}