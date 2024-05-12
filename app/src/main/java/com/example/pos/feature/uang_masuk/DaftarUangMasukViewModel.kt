package com.example.pos.feature.uang_masuk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pos.data.model.local.Record
import com.example.pos.repositories.RecordRepository
import com.example.pos.util.currentDate
import com.example.pos.util.parseDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DaftarUangMasukViewModel @Inject constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {
    val startDate = MutableLiveData<String>()
    val endDate = MutableLiveData<String>()

    init {
        startDate.value = currentDate()
        endDate.value = currentDate()
    }

    fun setStartDate(startDate: String) {
        this.startDate.value = startDate
    }

    fun setEndDate(endDate: String) {
        this.endDate.value = endDate
    }

    fun loadRecords(): LiveData<Map<String,List<Record>?>?>? {
        val startDate = startDate.value
        val endDate = endDate.value

        if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
            val millisStart = convertStart(startDate.parseDate())
            val millisEnd = convertEnd(endDate.parseDate())
            return recordRepository.getRecordsGroupByDate(millisStart, millisEnd)
                .asLiveData(viewModelScope.coroutineContext)
        }

        return null
    }
    private fun convertStart(milliSecond: Long) : Long{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSecond
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.timeInMillis
    }

    private fun convertEnd(milliSecond: Long) : Long{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSecond
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
    fun deleteRecords(id: Int) {
        viewModelScope.launch {
            recordRepository.deleteRecord(id)
        }
    }
}