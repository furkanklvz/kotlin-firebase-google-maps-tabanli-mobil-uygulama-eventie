package com.example.gezirehberi.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gezirehberi.R
import com.example.gezirehberi.CreatedEventDescriptionActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecyclerAdapterEventOwner(val createdEventList : MutableList<String>, val eventDateList : MutableList<Date>, val eventIDList : MutableList<String>) : RecyclerView.Adapter<RecyclerAdapterEventOwner.DuzenledigimEtkinliklerVH>() {
    class DuzenledigimEtkinliklerVH(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DuzenledigimEtkinliklerVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row_created,parent,false)
        return DuzenledigimEtkinliklerVH(itemView)
    }

    override fun getItemCount(): Int {
        return createdEventList.size
    }

    override fun onBindViewHolder(holder: DuzenledigimEtkinliklerVH, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_recyclerVieweo_eventName).text = createdEventList.get(position)
        val date = SimpleDateFormat("dd/MM/yyyy",
            Locale.getDefault()).format(eventDateList.get(position))
        holder.itemView.findViewById<TextView>(R.id.tv_recyclerVieweo_eventDate).text = date
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CreatedEventDescriptionActivity::class.java)
            intent.putExtra("eventID",eventIDList.get(position))
            intent.putExtra("eventDate",date)
            holder.itemView.context.startActivity(intent)

        }
    }
}