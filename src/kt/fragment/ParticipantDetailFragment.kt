package com.example.gezirehberi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gezirehberi.databinding.FragmentParticipantDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class ParticipantDetailFragment : Fragment() {
    val db = Firebase.firestore
    lateinit var participantEmail : String
    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        fun newInstance(param1: String,param2: String): ParticipantDetailFragment {
            val fragment = ParticipantDetailFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentParticipantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParticipantDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.collection("users").document(param1!!).get().addOnSuccessListener { snapshot->
            if (snapshot != null) {
                binding.tvPdEMail.text = snapshot.get("eMail") as String
                binding.tvPdTelNo.text = snapshot.get("tel") as String
                participantEmail = snapshot.get("eMail") as String
            }
        }
        binding.btPdClose.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
        binding.btPdDeleteParticipant.setOnClickListener {
            db.collection("events").document(param2!!).get().addOnSuccessListener { snapshot->
                if (snapshot != null) {
                    val participants = snapshot.get("participants") as ArrayList<String>
                    participants.remove(participantEmail)
                    db.collection("events").document(param2!!).update("participants", participants).addOnSuccessListener {
                        db.collection("users").document(participantEmail).get().addOnSuccessListener { snapshot->
                            if(snapshot!=null){
                                val events = snapshot.get("joinedEvents") as ArrayList<String>
                                events.remove(param2!!)
                                db.collection("users").document(participantEmail).update("joinedEvents", events).addOnSuccessListener {
                                    Toast.makeText(context, "Üye silindi", Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.beginTransaction().remove(this).commit()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}