package com.example.gezirehberi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.gezirehberi.fragment.ParticipantDetailFragment
import com.example.gezirehberi.R

class RecyclerAdapterParticipants(val activity: AppCompatActivity, val participantsName: MutableList<String>, val participantsSurname: MutableList<String>, val participantsEmail: MutableList<String>, val eventID: String?): RecyclerView.Adapter<RecyclerAdapterParticipants.ParticipantsVH>()  {
    class ParticipantsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycle_view_row_show_participants,parent,false)
        return ParticipantsVH(itemView)
    }

    override fun getItemCount(): Int {
        return participantsName.size
    }

    override fun onBindViewHolder(holder: ParticipantsVH, position: Int) {
        val nameSurname: String = participantsName.get(position) + " " + participantsSurname.get(position)
        holder.itemView.findViewById<TextView>(R.id.tv_participant_name).text = nameSurname
        println(nameSurname)
        holder.itemView.setOnClickListener {
            val fragmentManager = activity.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val participantDetailFragment =
                ParticipantDetailFragment.newInstance(participantsEmail.get(position), eventID!!)
            fragmentTransaction.replace(R.id.fl_participantDetail,participantDetailFragment)
            fragmentTransaction.addToBackStack(null)  // Allow user to use back button to close the fragment
            fragmentTransaction.commit()
        }
    }
}