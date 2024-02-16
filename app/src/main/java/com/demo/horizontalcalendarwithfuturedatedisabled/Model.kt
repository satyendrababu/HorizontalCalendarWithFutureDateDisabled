package com.demo.horizontalcalendarwithfuturedatedisabled

import java.util.Date

data class Model(val date: Date, val day: String, var isSelected: Boolean = false)
