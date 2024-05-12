package com.example.pos.feature.input_uang_masuk

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.data.model.local.Record
import com.example.pos.repositories.RecordRepository
import com.example.pos.util.parseDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputUangMasukViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {
    private val mutableRecord = MutableLiveData<Record>()
    private val mutableIsValidated = MutableLiveData<Boolean>()

    val records: MutableLiveData<Record> = mutableRecord
    val isValidated: MutableLiveData<Boolean> = mutableIsValidated

    fun insert(
        from: String,
        to: String,
        date: String,
        total: String,
        note: String,
        type: String,
        imageUri: String
    ) {
        mutableRecord.value = Record(
            from = from,
            to = to,
            date = date.parseDate(),
            total = total,
            note = note,
            type = type,
            imageUri = imageUri
        )
        if (validationForm()) {
            Log.v("Test", "validate: True")
            mutableIsValidated.value = true
            viewModelScope.launch(Dispatchers.IO) {
                mutableRecord.value?.let { recordRepository.createRecord(it) }
            }

        } else {
            Log.v("Test", "validate: False")
            mutableIsValidated.value = false
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