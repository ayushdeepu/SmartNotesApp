package com.ayush.smartnotes.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Formats a millisecond timestamp as "12 Oct 2026, 04:35 PM" style text. */
fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(millis))
