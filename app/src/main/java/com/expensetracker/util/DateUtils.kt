package com.expensetracker.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getCurrentMonth(): String = String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)

    fun getCurrentYear(): String = Calendar.getInstance().get(Calendar.YEAR).toString()

    fun getCurrentMonthInt(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    fun getCurrentYearInt(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getMonthName(month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, month - 1)
        return SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.time)
    }

    fun getMonthYear(month: Int, year: Int): String = "${getMonthName(month)} $year"
}
