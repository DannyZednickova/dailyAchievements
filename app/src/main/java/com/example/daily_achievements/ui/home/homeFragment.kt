package com.example.daily_achievements.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    // Konstanty
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // Získání poskytovatele polohy
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inicializace poskytovatele polohy
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aktualizace času na obrazovce
        updateCurrentTime()

        // Nastavení posluchače pro tlačítko Přidat poznámku
        binding.buttonAddNote.setOnClickListener {
            val dialog = AddRecordDialogFragment { note ->
                displayNote(note)
            }
            dialog.show(parentFragmentManager, "AddRecordDialogFragment")
        }


        // Nastavení listenerů pro časová pole
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

        // Automatické získání polohy při načtení fragmentu
        getLastKnownLocation()
    }

    // Funkce pro aktualizaci aktuálního času
    private fun updateCurrentTime() {
        val currentTime = Calendar.getInstance().time
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        binding.textCurrentTime.text = "Aktuální čas: ${timeFormat.format(currentTime)}"
    }

    // Funkce pro zobrazení TimePicker a formátování času
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

    // Funkce pro získání poslední známé polohy
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
                    // Použití Geocoderu pro převod souřadnic na adresu
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


    // Metoda pro zobrazení poznámky pod geolokací
    private fun displayNote(note: String) {
        binding.textNote.text = "Poznámka: $note"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
