package com.demo.horizontalcalendarwithfuturedatedisabled

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.demo.horizontalcalendarwithfuturedatedisabled.databinding.DateItemBinding
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date

class AdapterForDates (
    private val mModelItems: ArrayList<Model>,
    private val mContext: Context,
    private val mDatesList: ArrayList<String>,
    private val fullFormatDate: ArrayList<Date>,
    private val dateItemClickListener: DateItemClickListener
): RecyclerView.Adapter<AdapterForDates.ViewHolder>() {

    var pos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DateItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mModelItems.size
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val mMonths = DateFormatSymbols().months
        viewHolder.date.text = mModelItems[position].date.date.toString()
        viewHolder.day.text = mModelItems[position].day.substring(0,3)

        viewHolder.date.setOnClickListener {
            val date = mDatesList[position]
            val dList = date.split("-")
            dateItemClickListener.onDateClick("${dList[1]}-${dList[0]}-${dList[2]}", position)
            val calendar = Calendar.getInstance()
            calendar.time = fullFormatDate[position]
            mMonth = mMonths[calendar.get(Calendar.MONTH)] + ", " + calendar.get(Calendar.YEAR)
            pos = position
            notifyDataSetChanged()
        }
        mModelItems[position].isSelected = pos == position
        if (mModelItems[position].isSelected) {
            viewHolder.date.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                )
            )
            viewHolder.imageView.background = mContext.getDrawable(R.drawable.highlightdate)
        }else {
            viewHolder.date.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.black
                )
            )
            viewHolder.imageView.background = null
        }

    }

    class ViewHolder(private val binding: DateItemBinding) : RecyclerView.ViewHolder(binding.root){
        internal val date = binding.date
        internal val day = binding.day
        internal val imageView = binding.circleImageView
    }
    fun updatePosition(pos: Int) {
        this.pos = pos
        notifyDataSetChanged()
    }
    companion object {
        var mMonth: String? = null
    }

}