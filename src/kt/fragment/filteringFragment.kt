package com.example.gezirehberi.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import com.example.gezirehberi.R
import com.example.gezirehberi.UpdateMarkersListener
import com.example.gezirehberi.databinding.FragmentFilteringBinding
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class filteringFragment : Fragment() {
    private lateinit var updateMarkersListener: UpdateMarkersListener
    val db = Firebase.firestore
    lateinit var spinnerCategory: Spinner
    lateinit var spinnerCity: Spinner
    var spinnerSelectedCategory: String? = null
    var spinnerSelectedCity: String? = null
    lateinit var checkBoxCity: CheckBox
    lateinit var checkBoxCategory: CheckBox
    lateinit var checkBoxPrice: CheckBox
    lateinit var editTextPriceMin: EditText
    lateinit var editTextPriceMax: EditText
    val filteredEvents: MutableList<GeoPoint> = mutableListOf()

    private var _binding: FragmentFilteringBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilteringBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        updateMarkersListener = context as UpdateMarkersListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerCategory = binding.spinnerCategory
        spinnerCity = binding.spinnerCity
        checkBoxCity = binding.cbCity
        checkBoxCategory = binding.cbCategory
        checkBoxPrice = binding.cbPrice
        editTextPriceMax = binding.etMaxPrice
        editTextPriceMin = binding.etMinPrice
        spinnerCategory.isEnabled = false
        spinnerCity.isEnabled = false
        editTextPriceMax.isEnabled = false
        editTextPriceMin.isEnabled = false

        checkBoxCity.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) spinnerCity.isEnabled = true
            else spinnerCity.isEnabled = false
        }
        checkBoxCategory.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) spinnerCategory.isEnabled = true
            else spinnerCategory.isEnabled = false
        }
        checkBoxPrice.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                editTextPriceMax.isEnabled = true
                editTextPriceMin.isEnabled = true
            } else {
                editTextPriceMax.isEnabled = false
                editTextPriceMin.isEnabled = false
            }
        }

        val categoryArrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories_array,
            R.layout.dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCategory.adapter = categoryArrayAdapter
        val cityArrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cities_array,
            R.layout.dropdown_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCity.adapter = cityArrayAdapter

        spinnerCity.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerSelectedCity = p0!!.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        spinnerCategory.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                spinnerSelectedCategory = p0!!.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        binding.btnApplyFilters.setOnClickListener {
            filteredEvents.clear()

            if (checkBoxPrice.isChecked) {
                when {
                    editTextPriceMin.text.isEmpty() && editTextPriceMax.text.isEmpty() -> checkBoxPrice.isChecked = false
                    editTextPriceMin.text.isEmpty() -> editTextPriceMin.setText("0")
                    editTextPriceMax.text.isEmpty() -> editTextPriceMax.setText("99999")
                }
            }

            var query: Query = db.collection("events")

            if (checkBoxCategory.isChecked) {
                query = query.whereEqualTo("category", spinnerSelectedCategory)
            }
            if (checkBoxCity.isChecked) {
                query = query.whereEqualTo("city", spinnerSelectedCity)
            }
            if (checkBoxPrice.isChecked) {
                val minPrice = editTextPriceMin.text.toString().toInt()
                val maxPrice = editTextPriceMax.text.toString().toInt()
                query = query.whereGreaterThanOrEqualTo("price", minPrice)
                    .whereLessThanOrEqualTo("price", maxPrice)
            }

            query.get().addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    for (event in snapshot.documents) {
                        filteredEvents.add(event.get("locationLatLng") as GeoPoint)
                    }
                    updateMarkersListener.updateMarkers(filteredEvents)
                }
            }.addOnFailureListener { e->
                println(e.localizedMessage)
            }

            parentFragmentManager.beginTransaction().hide(this).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
