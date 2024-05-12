package com.example.pos.data.model.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
class Record(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "to")
    var to: String? = "",

    @ColumnInfo(name = "from")
    var from: String? = "",

    @ColumnInfo(name = "total")
    var total: String? = "",

    @ColumnInfo(name = "note")
    var note: String? = "",

    @ColumnInfo(name = "type")
    var type: String? = "",

    @ColumnInfo(name = "date")
    var date: Long? = 0,

    @ColumnInfo(name = "imageUri")
    var imageUri: String? = "",
) : Parcelable