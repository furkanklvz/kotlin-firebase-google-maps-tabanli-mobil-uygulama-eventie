package com.example.gezirehberi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gezirehberi.R
import com.example.gezirehberi.JoinedEventDescriptionActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerAdapter(val eventList : MutableList<String>, val eventDateList : MutableList<Date>,val eventIDList : MutableList<String>) : RecyclerView.Adapter<RecyclerAdapter.KatildigimEtkinliklerVH>() {
    class KatildigimEtkinliklerVH(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KatildigimEtkinliklerVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_participated,parent,false)
        return KatildigimEtkinliklerVH(itemView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: KatildigimEtkinliklerVH, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_recyclerView_eventName).text = eventList.get(position)
        val date = SimpleDateFormat("dd/MM/yyyy",
            Locale.getDefault()).format(eventDateList.get(position))
        holder.itemView.findViewById<TextView>(R.id.tv_recyclerView_eventDate).text = date
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, JoinedEventDescriptionActivity::class.java)
            intent.putExtra("eventID",eventIDList.get(position))
            intent.putExtra("eventDate",date)
            holder.itemView.context.startActivity(intent)
        }
    }
}