package com.example.daily_achievements.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.daily_achievements.databinding.FragmentHomeBinding
import com.example.daily_achievements.ui.addRecord.AddRecordDialogFragment

class homeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // bunde objekt - tam se uklada stavy te aplikace atp., jsou to vypocitana data. OnSaveInstanceSave
    //neni persistentni uloziste, preziva to konfiguracni stavy a aplikace jde do nachvilku
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this).get(homeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        //inflator je podstrkovatko layoutu a je to dostupne kazde aktivite

        val root: View = binding.root
        // binding root = ukazatel na tu zakladni sablonu

        // pomoci tohoto bindingu se mi zobrazuji prvky na layoutu
        val textView: TextView = binding.textHome



        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.buttonAddRecord.setOnClickListener {
            val dialog = AddRecordDialogFragment()
            dialog.show(parentFragmentManager, "AddRecordDialog")
        }


        return root
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}