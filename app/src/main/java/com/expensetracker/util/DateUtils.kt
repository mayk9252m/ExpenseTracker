package com.expensetracker.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getCurrentMonth(): String =
        String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1)

    fun getCurrentYear(): String =
        Calendar.getInstance().get(Calendar.YEAR).toString()

    fun getCurrentMonthInt(): Int =
        Calendar.getInstance().get(Calendar.MONTH) + 1

    fun getCurrentYearInt(): Int =
        Calendar.getInstance().get(Calendar.YEAR)

    // ✅ Returns timestamp for first millisecond of the month
    fun getStartOfMonth(month: Int = getCurrentMonthInt(), year: Int = getCurrentYearInt()): Long {
        return Calendar.getInstance().apply {
            set(year, month - 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // ✅ Returns timestamp for last millisecond of the month
    fun getEndOfMonth(month: Int = getCurrentMonthInt(), year: Int = getCurrentYearInt()): Long {
        return Calendar.getInstance().apply {
            set(year, month - 1, 1, 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }

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