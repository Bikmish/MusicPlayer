package com.msu.cmc.musicplayer.presentation.list.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msu.cmc.musicplayer.models.Player
import com.msu.cmc.musicplayer.R
import com.msu.cmc.musicplayer.models.TrackItem
import com.msu.cmc.musicplayer.presentation.list.IOnGalleryItemClicked


class TracklistAdapter(private val dataSet: ArrayList<TrackItem>, private val context: Context, private val onClickAction: IOnGalleryItemClicked): RecyclerView.Adapter<TracklistAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val title: TextView
        val icon: ImageView
        init {
            title = view.findViewById(R.id.music_title)
            icon = view.findViewById(R.id.icon_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.track_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trackData: TrackItem = dataSet.get(position)
        holder.title.setText(trackData.title)

        if(Player.currentIndex ==position){
            holder.title.setTextColor(Color.parseColor("#1550F5"));
        }else{
            holder.title.setTextColor(Color.parseColor("#000000"));
        }

        holder.itemView.setOnClickListener{
                Player.getInstance()!!.reset()
                Player.currentIndex = position

                onClickAction.onGalleryItemClicked()
        }
    }

    override fun getItemCount(): Int = dataSet.size
}