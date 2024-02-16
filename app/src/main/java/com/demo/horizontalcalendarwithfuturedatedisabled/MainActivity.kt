package com.demo.horizontalcalendarwithfuturedatedisabled

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.demo.horizontalcalendarwithfuturedatedisabled.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity(), DateItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private var horizontalCalendar: HorizontalCalendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            month.setOnClickListener {
                showDatePickerDialogFutureDateDisabled()
            }
        }
        horizontalCalendar = HorizontalCalendar(this, binding.datesRv, binding.month, this@MainActivity )

    }

    override fun onDateClick(date: String, position: Int) {
        Toast.makeText(this, "Date : "+ date, Toast.LENGTH_LONG).show()
    }
    private fun showDatePickerDialogFutureDateDisabled() {
        val cal = Calendar.getInstance()
        val dateListener =
            DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                horizontalCalendar!!.setCalendarDate(year, monthOfYear, dayOfMonth, this, binding.month)

            }
        val datePicker  = DatePickerDialog(this, dateListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
        binding.month.setOnClickListener {
            val datePicker  = DatePickerDialog(this, dateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }
}