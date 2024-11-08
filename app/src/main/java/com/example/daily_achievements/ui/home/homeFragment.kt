package com.example.daily_achievements.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.daily_achievements.databinding.FragmentHomeBinding
import com.example.daily_achievements.ui.addRecord.AddRecordDialogFragment
import android.app.TimePickerDialog
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class homeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var note: String? = null // Poznámka, kterou chceme uchovat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCurrentTime()
        binding.buttonAddNote.setOnClickListener {
            val dialog = AddRecordDialogFragment { noteText ->
                note = noteText // Uložíme poznámku do proměnné
                displayNote(noteText)
            }
            dialog.show(parentFragmentManager, "AddRecordDialogFragment")
        }

        binding.editTextWakeUpTime.setOnClickListener {
            showTimePicker { selectedTime ->
                binding.editTextWakeUpTime.setText(selectedTime)
            }
        }

        binding.editTextSleepTime.setOnClickListener {
            showTimePicker { selectedTime ->
                binding.editTextSleepTime.setText(selectedTime)
            }
        }

        getLastKnownLocation()
    }

    private fun updateCurrentTime() {
        val currentTime = Calendar.getInstance().time
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        binding.textCurrentTime.text = "Aktuální čas: ${timeFormat.format(currentTime)}"
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val city = addresses[0]?.locality ?: "Neznámé město"
                            val country = addresses[0]?.countryName ?: "Neznámá země"
                            val locationText = "Místo: $city, $country"
                            binding.textLocation.text = locationText
                        } else {
                            binding.textLocation.text = "Místo není dostupné"
                        }
                    }
                } ?: run {
                    binding.textLocation.text = "Poloha není dostupná"
                }
            }
            .addOnFailureListener {
                binding.textLocation.text = "Chyba při získávání polohy"
            }
    }

    private fun displayNote(note: String) {
        binding.textNote.text = "Poznámka: $note"
    }

    // Uložení stavu poznámky při změně konfigurace
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("note", note)
    }

    // Obnovení poznámky po změně konfigurace
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        note = savedInstanceState?.getString("note")
        note?.let {
            displayNote(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
