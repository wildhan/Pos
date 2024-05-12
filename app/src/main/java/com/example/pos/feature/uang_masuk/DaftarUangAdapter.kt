package com.example.pos.feature.uang_masuk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.R
import com.example.pos.data.model.local.Record
import com.example.pos.databinding.ItemRvGroupUangMasukBinding
import com.example.pos.databinding.ItemRvUangMasukBinding
import com.example.pos.util.DEFAULT_DATE_FORMAT
import com.example.pos.util.formatDate
import com.example.pos.util.parseDate
import com.example.pos.util.toRpFormat

class DaftarUangAdapter(
    private var recordsByDate: Map<String, List<Record>?>?,
    private val listener: (Record, Boolean) -> Unit
) :
    RecyclerView.Adapter<DaftarUangAdapter.DaftarUangVH>() {

        private var keySet = if (recordsByDate.isNullOrEmpty()) arrayOf() else recordsByDate!!.keys.toTypedArray()
    class DaftarUangVH(private val binding: ItemRvGroupUangMasukBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val FORMAT_DATE = "HH:mm"
        fun bind(groupName: String, records: List<Record>?, listener: (Record, Boolean) -> Unit) {

            binding.apply {
                tvGroupName.text = groupName
                var groupTotal = 0L
                llChild.removeAllViews()
                records?.forEach {
                    llChild.addView(childBinding(it,listener))
                    groupTotal += it.total!!.toLong()
                }
                tvGroupTotal.text = groupTotal.toRpFormat()
            }
        }

        private fun childBinding(record: Record, listener: (Record, Boolean) -> Unit) : View {
            val childBinding = ItemRvUangMasukBinding.inflate(
                LayoutInflater.from(itemView.context),
                null,
                false
            )
            childBinding.apply {
                tvInfo?.text =
                    itemView.context.getString(R.string.text_from_to, record.from, record.to)
                tvFrom?.text = record.from
                tvTo?.text = record.to
                tvNotes.text = record.note
                tvDate.text = record.date?.formatDate(FORMAT_DATE)
                tvAmount.text = record.total!!.toLong().toRpFormat()
                btnEdit?.setOnClickListener {
                    listener.invoke(record, true)
                }
                btnDelete?.setOnClickListener {
                    listener.invoke(record, false)
                }
            }
            return childBinding.root
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarUangVH =
        DaftarUangVH(
            ItemRvGroupUangMasukBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = keySet.size

    override fun onBindViewHolder(holder: DaftarUangVH, position: Int) {
        val key = keySet[position]
        holder.bind(key, recordsByDate?.get(key), listener)
    }

    fun setRecords(newRecords: Map<String, List<Record>?>?) {
        recordsByDate = newRecords
        keySet = if (recordsByDate.isNullOrEmpty()) arrayOf() else {
            recordsByDate!!.keys.toTypedArray().sortedBy {
                it.parseDate(DEFAULT_DATE_FORMAT)
            }.toTypedArray()
        }
        notifyDataSetChanged()
    }
}