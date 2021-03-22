package com.example.googlemaps

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val fileName = "markers"
    private lateinit var mMap: GoogleMap
    private lateinit var markers: MutableList<Pair<Double, Double>>
    private lateinit var latEditText: EditText
    private lateinit var lngEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        latEditText = findViewById(R.id.latitudeEditText)
        lngEditText = findViewById(R.id.longitudeEditText)
    }

    override fun onStop() {
        super.onStop()

        val fileOutput = openFileOutput(fileName, Context.MODE_PRIVATE)
        val objectOutput = ObjectOutputStream(fileOutput)
        objectOutput.writeObject(markers)
        objectOutput.close()
        fileOutput.close()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val path = getFileStreamPath(fileName)
        if (path.exists()) {
            val fileInput = openFileInput(fileName)
            val objectInput = ObjectInputStream(fileInput)
            markers = objectInput.readObject() as MutableList<Pair<Double, Double>>
            for (marker in markers) {
                val lat = marker.first
                val lng = marker.second
                mMap.addMarker(MarkerOptions().position(LatLng(lat, lng)).title("$lat $lng"))
            }
            objectInput.close()
            fileInput.close()
        } else {
            markers = mutableListOf()
        }
    }

    fun addMarker(view: View) {
        val lat = latEditText.text.toString().toDouble()
        val lng = lngEditText.text.toString().toDouble()

        val marker = LatLng(lat, lng)
        markers.add(Pair(lat, lng))

        mMap.addMarker(MarkerOptions().position(marker).title("$lat $lng"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker))
    }
}