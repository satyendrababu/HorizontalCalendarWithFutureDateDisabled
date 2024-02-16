package com.demo.horizontalcalendarwithfuturedatedisabled

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HorizontalCalendar(
    dateItemClickListener: DateItemClickListener,
    dates_rv: RecyclerView,
    month: TextView,
    private var context: Context
) {
    private var mFirstCompleteVisibleItemPosition = -1
    private var mLastCompleteVisibleItemPosition = -1
    private var mEndDate: Date? = null
    private var mMonths = DateFormatSymbols().months
    private var mStartD: Date? = null
    private lateinit var mBaseDateList: ArrayList<Date>
    private val mCal = Calendar.getInstance()
    private var dates_rv: RecyclerView? = dates_rv
    private var month: TextView? = month
    private var mFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private lateinit var adapterForDates: AdapterForDates
    private val mFinalDateList = ArrayList<Model>()

    init {
        horizontalDates(dateItemClickListener)
    }

    private fun horizontalDates(dateItemClickListener: DateItemClickListener) {
        mFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        mStartD = Date()
        val calendar = Calendar.getInstance()
        calendar.time = mStartD
        val currentMonth = mMonths[calendar.get(Calendar.MONTH)]
        val currentYear = calendar.get(Calendar.YEAR).toString()
        month?.text = "$currentMonth, $currentYear"
        calendar.add(Calendar.MONTH, -1)
        mEndDate = calendar.time
        mBaseDateList = getDates(mFormatter.format(mEndDate), mFormatter.format(mStartD))
        setAdapter(mBaseDateList, dateItemClickListener)
        val layoutManager3 = dates_rv?.layoutManager as LinearLayoutManager
        layoutManager3.scrollToPosition(mBaseDateList.size-1)

    }

    private fun setAdapter(dates: ArrayList<Date>, dateItemClickListener: DateItemClickListener) {
        val finalDates = ArrayList<Model>()
        val clickedDate = ArrayList<String>()
        for (i in 0 until dates.size) {
            val lDate = dates[i]
            val c = Calendar.getInstance()
            c.time = lDate

            val dayOfWeek = c.get(Calendar.DATE)
            val month = c.get(Calendar.MONTH)
            val year = c.get(Calendar.YEAR)

            val dayLongName = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
            finalDates.add(Model(c.time, dayLongName))
            clickedDate.add(String.format("%02d-%02d-%04d", dayOfWeek, month + 1, year))
        }
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
        layoutManager.stackFromEnd = true
        dates_rv?.layoutManager = layoutManager
        layoutManager.scrollToPosition(30)
        adapterForDates = AdapterForDates(finalDates, context, clickedDate, dates, dateItemClickListener)
        dates_rv?.adapter = adapterForDates

        dates_rv?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManagerForPos = recyclerView.layoutManager
                val totalItemCount = layoutManagerForPos?.itemCount
                if (layoutManagerForPos is GridLayoutManager) {
                    val gridLayoutManager = layoutManagerForPos as GridLayoutManager?
                    mFirstCompleteVisibleItemPosition =
                        gridLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    mLastCompleteVisibleItemPosition =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition()
                } else if (layoutManagerForPos is LinearLayoutManager) {
                    val linearLayoutManager = layoutManagerForPos as LinearLayoutManager?
                    mFirstCompleteVisibleItemPosition =
                        linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    mLastCompleteVisibleItemPosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition()
                }
                if (mFirstCompleteVisibleItemPosition == 0) {
                    if (dy < 0 || dx < 0) {
                        if (dx < 0){
                            val calendar  = Calendar.getInstance()
                            calendar.time = mEndDate
                            calendar.add(Calendar.MONTH, -1)
                            val tempEndDate = calendar.time
                            month?.text =
                                mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
                            mBaseDateList =
                                getDates(mFormatter.format(tempEndDate), mFormatter.format(mStartD))
                            setAdapter(mBaseDateList, dateItemClickListener)
                            mEndDate = tempEndDate

                        }
                    }
                }else if (totalItemCount != null) {
                    if (mLastCompleteVisibleItemPosition == totalItemCount - 1) {
                        val calendar = Calendar.getInstance()
                        val date = mBaseDateList[mLastCompleteVisibleItemPosition]
                        calendar.time = date
                        month?.text =
                            mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)

                        if (dy > 0 || dx >0) {
                            if (dy > 0) {
                                Log.d("status", "Scrolled Top")
                            }
                            if (dx > 0) {
                                Log.d("status", "Scrolled Right")
                            }
                        }
                    } else {
                        if (dx < 0) {
                            Log.d("status", "Scrolled Left")
                            val calendar = Calendar.getInstance()
                            val date = mBaseDateList[mFirstCompleteVisibleItemPosition + 1]
                            calendar.time = date
                            month?.text =
                                mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
                        } else {
                            val calendar = Calendar.getInstance()
                            val date = mBaseDateList[mFirstCompleteVisibleItemPosition]
                            calendar.time = date
                            month?.text =
                                mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
                        }
                    }
                }
            }
        })
        mFinalDateList.addAll(finalDates)
        for (i in finalDates.indices) {
            if (mFormatter.format(finalDates[i].date) == mFormatter.format(mCal.time)) {
                adapterForDates.updatePosition(i)
            }
        }
    }
    internal fun setCalendarDate(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        dateItemClickListener: DateItemClickListener,
        monthText: TextView
    ){
        val finalDates = ArrayList<Model>()
        mCal.set(Calendar.YEAR, year)
        mCal.set(Calendar.MONTH, month)
        mCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val calendarSelect = Calendar.getInstance()
        calendarSelect.time = mCal.time
        calendarSelect.add(Calendar.MONTH, -1)
        val endDate = calendarSelect.time
        mBaseDateList = getDates(mFormatter.format(endDate), mFormatter.format(mStartD))
        setAdapter(mBaseDateList, dateItemClickListener)
        mEndDate = endDate
        val da = "${month + 1}-${dayOfMonth}-${year}"
        dateItemClickListener.onDateClick(da, -1)
        if (mBaseDateList.size > 30) {
            dates_rv?.smoothScrollToPosition(33)
        } else {
            dates_rv?.smoothScrollToPosition(30)
        }
        dates_rv?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManagerForPos = recyclerView.layoutManager
                val totalItemCount = layoutManagerForPos?.itemCount
                if (layoutManagerForPos is GridLayoutManager) {
                    val gridLayoutManager = layoutManagerForPos as GridLayoutManager?
                    mFirstCompleteVisibleItemPosition =
                        gridLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    mLastCompleteVisibleItemPosition =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition()
                } else if (layoutManagerForPos is LinearLayoutManager) {
                    val linearLayoutManager = layoutManagerForPos as LinearLayoutManager?
                    mFirstCompleteVisibleItemPosition =
                        linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    mLastCompleteVisibleItemPosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition()
                }
                if (mFirstCompleteVisibleItemPosition == 0) {
                    if (dy < 0 || dx < 0) {
                        if (dx < 0){
                            val calendar  = Calendar.getInstance()
                            calendar.time = mEndDate
                            calendar.add(Calendar.MONTH, -1)
                            val tempEndDate = calendar.time
                            monthText?.text =
                                mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
                            mBaseDateList =
                                getDates(mFormatter.format(tempEndDate), mFormatter.format(mStartD))
                            setAdapter(mBaseDateList, dateItemClickListener)
                            mEndDate = tempEndDate

                        }
                    }
                }else if (totalItemCount != null) {
                    if (mLastCompleteVisibleItemPosition == totalItemCount - 1) {
                        val calendar = Calendar.getInstance()
                        val date = mBaseDateList[mLastCompleteVisibleItemPosition]
                        calendar.time = date
                        monthText?.text =
                            mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)

                        if (dy > 0 || dx >0) {
                            if (dy > 0) {
                                Log.d("status", "Scrolled Top")
                            }
                            if (dx > 0) {
                                Log.d("status", "Scrolled Right")
                            }
                        }
                    } else {
                        if (dx < 0) {
                            Log.d("status", "Scrolled Left")
                            val calendar = Calendar.getInstance()
                            val date = mBaseDateList[mFirstCompleteVisibleItemPosition + 1]
                            calendar.time = date
                            monthText?.text =
                                mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
                        } else {
                            val calendar = Calendar.getInstance()
                            val date = mBaseDateList[mFirstCompleteVisibleItemPosition]
                            calendar.time = date
                            monthText?.text =
                                mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
                        }
                    }
                }
            }
        })
        mFinalDateList.addAll(finalDates)
        for (i in finalDates.indices) {
            if (mFormatter.format(finalDates[i].date) == mFormatter.format(mCal.time)) {
                adapterForDates.updatePosition(i)
            }
        }
    }

    private fun getDates(dateString1: String, dateString2: String): ArrayList<Date> {
        val mDates = ArrayList<Date>()
        var startDate: Date? = null
        startDate = mFormatter.parse(dateString1)
        var endDate: Date? = null
        endDate = mFormatter.parse(dateString2)
        val interval = (24 *1000 * 60 * 60).toLong()
        val endTime = endDate.time
        var currentTime = startDate.time
        while (currentTime <= endTime) {
            mDates.add(Date(currentTime))
            currentTime += interval
        }
        return mDates
    }


}