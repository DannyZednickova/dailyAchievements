package com.example.daily_achievements.ui.addRecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.daily_achievements.databinding.DialogAddRecordBinding

class AddRecordDialogFragment(private val onNoteSaved: (String) -> Unit) : DialogFragment() {

    private var _binding: DialogAddRecordBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nastavení posluchače pro tlačítko Uložit
        binding.buttonSave.setOnClickListener {
            val note = binding.editTextName.text.toString()
            onNoteSaved(note) // Předání poznámky zpět do homeFragment
            dismiss() // Zavření dialogu po uložení
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


