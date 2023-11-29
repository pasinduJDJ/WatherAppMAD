package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.picasso.Picasso
import java.io.IOException

class LocationSelectPage : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var button : Button
    private lateinit var txtCityName: TextView
    private lateinit var btnCityFind : Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val data = arrayOf("Suggest Locations","Colombo", "Kandy", "Nugegoda")

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select_page)

        spinner= findViewById(R.id.citySpinner)
        button= findViewById(R.id.conformBtn)
        txtCityName= findViewById(R.id.txtCityName)
        btnCityFind= findViewById(R.id.btnCityFind)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        spinner()
        getCurrentLocationAndLoadWeather()
    }

    private fun getCurrentLocationAndLoadWeather() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        // Update TextView with live location using Geocoder
                        updateLocationTextView(location.latitude, location.longitude)

                        // Load weather information based on current location
//                        loadWeatherInfo(location.latitude, location.longitude)
                    } else {
                        showErrorToast("Location not available")
                    }
                }
                .addOnFailureListener { e ->
                    dismissProgressDialog()
                    Log.e("Location", "Error getting location", e)
                    showErrorToast("Error getting location")
                }
        } catch (e: SecurityException) {
            dismissProgressDialog()
            Log.e("Location", "Security exception: ${e.message}")
            showErrorToast("Location permission denied")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationTextView(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this)
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            null
        }

        val cityName = addresses?.firstOrNull()?.locality

        if (cityName != null) {
            txtCityName.text = "$cityName"
        } else {
            txtCityName.text = "$latitude, Lon: $longitude"
        }
        btnCityFind.setOnClickListener {
            // Move to the second page and pass the selected value
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("selectedValue", cityName)
            startActivity(intent)
        }
    }


    private fun spinner(){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        var selectedValue: String? = null

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedValue = data[position]
//                showToast(data[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                showToast("Button Clicked!")
            }
        }
        button.setOnClickListener {
            // Move to the second page and pass the selected value
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("selectedValue", selectedValue)
            startActivity(intent)
        }
    }
    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage(message)
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }



}