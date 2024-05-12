package com.example.pos.feature.edit_uang_masuk

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos.data.model.local.Record
import com.example.pos.repositories.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditUangMasukViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {
    private val mutableRecord = MutableLiveData<Record>()
    private val mutableIsValidated = MutableLiveData<Boolean>()

    val isValidated: MutableLiveData<Boolean> = mutableIsValidated

    fun insert(
        from: String,
        to: String,
        total: String,
        note: String,
        type: String,
        date: Long?,
        imageUri: String
    ) {
        mutableRecord.value = mutableRecord.value?.let {
            Record(
                id = it.id,
                from = from,
                to = to,
                total = total,
                note = note,
                type = type,
                date = date,
                imageUri = imageUri
            )
        }
        if (validationForm()) {
            Log.v("Test", "validate: True")
            mutableIsValidated.value = true
            viewModelScope.launch(Dispatchers.IO) {
                mutableRecord.value?.let { recordRepository.updateRecord(it) }
            }

        } else {
            Log.v("Test", "validate: False")
            mutableIsValidated.value = false
        }
    }

    fun getRecord(id: Int): LiveData<List<Record>?> {
        return recordRepository.getRecordById(id).asLiveData(viewModelScope.coroutineContext)
    }

    fun setupRecord(record: Record?) {
        if (record != null){
            mutableRecord.value = record!!
        }
    }

    private fun validationForm(): Boolean {
        val record = mutableRecord.value
        if (record?.from.isNullOrEmpty()) {
            return false
        } else if (record?.to.isNullOrEmpty()) {
            return false
        } else if (record?.total.isNullOrEmpty()) {
            return false
        } else if (record?.note.isNullOrEmpty()) {
            return false
        } else if (record?.type.isNullOrEmpty()) {
            return false
        }
        return true
    }
}