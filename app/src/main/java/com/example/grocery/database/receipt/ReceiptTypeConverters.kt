package com.example.grocery.database.receipt

import androidx.room.TypeConverter
import java.util.*

class ReceiptTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromItems(items: MutableList<String>): String? {
        var result: String = ""
        items.forEach {
            result += it
            result += ";" //TODO is using a semicolon here really a good idea?
        }
        return result
    }
    @TypeConverter
    fun toItems(megaString: String?): MutableList<String>? {
        var result =  megaString?.split(';')?.toMutableList()
        result?.removeLast() //last element is always empty
        return result
    }

    @TypeConverter
    fun fromPrices(items: MutableList<Float>): String? {
        var result: String = ""
        items.forEach {
            result += it.toString()
            result += ";" //TODO semicolon again. All of this is bad, prb
        }
        return result
    }
    @TypeConverter
    fun toIPrices(megaString: String?): MutableList<Float>? {
        var listOfFloatsInStringForm =  megaString?.split(';')?.toMutableList()
        listOfFloatsInStringForm?.removeLast() //last element is always empty
        var result : MutableList<Float> = mutableListOf()
        listOfFloatsInStringForm?.forEach {
            result.add(it.toFloat())
        }
        return result
    }

}