package com.example.pos.feature.uang_masuk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos.R
import com.example.pos.data.model.local.Record
import com.example.pos.databinding.FragmentDaftarUangMasukBinding
import com.example.pos.util.NavGraph
import com.example.pos.util.formatDate
import com.example.pos.util.parseDate
import com.example.pos.util.parseFormatDate
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DaftarUangMasukFragment : Fragment() {
    private val viewModel: DaftarUangMasukViewModel by activityViewModels()
    private lateinit var binding: FragmentDaftarUangMasukBinding
    private lateinit var adapter: DaftarUangAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaftarUangMasukBinding.inflate(inflater, container, false)

        adapter = DaftarUangAdapter(null) { record, isEdit ->
            if (isEdit) {
                navigateEditUang(record.id)
            } else {
                viewModel.deleteRecords(record.id)
                loadAllRecords()
            }
        }
        initUi()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadAllRecords()
    }

    private fun initUi() {
        binding.apply {
            viewModel.startDate.observe(viewLifecycleOwner) {
                tvFilter.text =
                    "${it?.parseFormatDate()} - ${viewModel.endDate.value?.parseFormatDate()}"
            }

            viewModel.endDate.observe(viewLifecycleOwner) {
                tvFilter.text =
                    "${viewModel.startDate.value?.parseFormatDate()} - ${it?.parseFormatDate()}"
            }

            rvList.layoutManager = LinearLayoutManager(context)
            rvList.adapter = adapter

            btnFilter.setOnClickListener {
                dialogDatePicker()
            }

            btnToInput.setOnClickListener {
                navigateInputUang()
            }
        }
    }

    private fun loadAllRecords() {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadRecords()?.observe(viewLifecycleOwner) { records ->
                initRecords(records)
            }
        }
    }

    private fun initRecords(groupRecords: Map<String,List<Record>?>?) {
        binding.apply {
            if (!groupRecords.isNullOrEmpty()) {
                tvEmptyRecord.visibility = View.GONE
            } else {
                tvEmptyRecord.visibility = View.VISIBLE
            }
            adapter.setRecords(groupRecords)
        }
    }

    private fun dialogDatePicker() {
        val dialog = MaterialDatePicker.Builder
            .dateRangePicker()
            .setPositiveButtonText("Pilih")
            .setSelection(
                Pair(
                    viewModel.startDate.value?.parseDate(),
                    viewModel.startDate.value?.parseDate()
                )
            )
            .build()
        dialog.addOnPositiveButtonClickListener {
            viewModel.setStartDate(it.first.formatDate())
            viewModel.setEndDate(it.second.formatDate())
            loadAllRecords()
            dialog.dismiss()
        }
        dialog.show(childFragmentManager, "DATE_PICKER")
    }

    private fun navigateInputUang() {
        NavGraph.navigateFragment(
            binding.root,
            R.id.action_daftarUangMasukFragment_to_inputUangMasukFragment
        )
    }

    private fun navigateEditUang(id: Int) {
        val bundle = bundleOf("id" to id)
        NavGraph.navigateFragment(
            binding.root,
            R.id.action_daftarUangMasukFragment_to_editUangMasukFragment,
            bundle = bundle
        )
    }


}