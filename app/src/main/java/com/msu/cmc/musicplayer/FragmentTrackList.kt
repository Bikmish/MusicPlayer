package com.msu.cmc.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FragmentTrackList: Fragment(R.layout.fragment_track_list), IOnGalleryItemClicked {
    lateinit var recyclerView: RecyclerView
    lateinit var notracksTextView: TextView
    var trackList: ArrayList<TrackItem> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recycler_view)
        notracksTextView = view.findViewById(R.id.notracks_text)

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
        val resolver = requireActivity().contentResolver
        val cursor: Cursor? = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, conditions, null, null)
        while (cursor!!.moveToNext()) {
            val trackData = TrackItem(cursor.getString(0), cursor.getString(1), cursor.getString(2))
            if (File(trackData.path!!).exists()) trackList.add(trackData)
        }

        if(trackList.size==0) {
            notracksTextView.setVisibility(View.VISIBLE);
        }else {
            recyclerView.layoutManager = LinearLayoutManager(requireActivity())
            recyclerView.adapter = TracklistAdapter(trackList, requireActivity(), this)
        }
    }

    fun checkPermission(): Boolean = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(requireActivity(), "Enable read permission in settings please", Toast.LENGTH_SHORT).show()
        } else
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 123)
    }

    override fun onResume() {
        super.onResume()
        recyclerView.adapter = TracklistAdapter(trackList, requireActivity(), this)
    }

    override fun onGalleryItemClicked() {
        val bundle = Bundle().apply{
            putSerializable("LIST", trackList)
        }
        parentFragmentManager.setFragmentResult("result", bundle)
        parentFragmentManager.beginTransaction()
            .add(R.id.fmt_tracklist, FragmentPlayer())
            .addToBackStack(null)
            .commit()
    }
}