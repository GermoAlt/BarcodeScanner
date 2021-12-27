package com.assa.barcodereader.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assa.barcodereader.R
import com.assa.barcodereader.main.weight_list.WeightAdapter
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode


private const val CAMERA_REQUEST_CODE = 101
private const val RC_PERMISSION = 10


class MainActivity() : AppCompatActivity() {

    private val mCodeScanner: CodeScanner? = null
    private var mPermissionGranted = false
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weightList = findViewById<RecyclerView>(R.id.weight_list)
        var weights : ArrayList<String> = arrayListOf()
        val weightAdapter = WeightAdapter(this, weights)
        weightList.adapter = weightAdapter
        weightList.layoutManager = LinearLayoutManager(this)

        var lastScanned = ""
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                if (!it.text.equals(lastScanned)) {
                    weights.add(0, it.text)
                    weightAdapter.notifyItemInserted(0)
                    lastScanned = it.text
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
            mPermissionGranted = false;
            requestPermissions(listOf(Manifest.permission.CAMERA).toTypedArray(), RC_PERMISSION);
        } else {
            mPermissionGranted = true;
        }
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


}