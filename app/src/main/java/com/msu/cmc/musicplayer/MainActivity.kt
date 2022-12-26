package com.msu.cmc.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var notracksTextView: TextView
    var trackList: ArrayList<TrackItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        notracksTextView = findViewById(R.id.notracks_text)

        if(!checkPermission()) {
            requestPermission()
            return
        }

        val columns = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )
        val conditions = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val cursor: Cursor? = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, conditions, null, null)
        while (cursor!!.moveToNext()) {
            val trackData = TrackItem(cursor.getString(0), cursor.getString(1), cursor.getString(2))
            if (File(trackData.path!!).exists()) trackList.add(trackData)
        }

        if(trackList.size==0) {
            notracksTextView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = TracklistAdapter(trackList, applicationContext)
        }

    }

    fun checkPermission(): Boolean = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this@MainActivity, "Enable read permission in settings please", Toast.LENGTH_SHORT).show()
        } else
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
    }

    override fun onResume() {
        super.onResume()
        if (recyclerView != null) {
            recyclerView.adapter = TracklistAdapter(trackList, applicationContext)
        }
    }
}