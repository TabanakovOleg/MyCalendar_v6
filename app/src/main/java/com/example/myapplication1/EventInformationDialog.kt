package com.example.myapplication1

import Event
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.text.CaseMap
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.constraintlayout.widget.ConstraintLayout
import java.lang.StringBuilder
import java.time.LocalDateTime
import java.util.*


class EventInformationDialog (val pickedEvent: Event, val position: Int):  AppCompatDialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    @RequiresApi(Build.VERSION_CODES.O)
    var newYear = pickedEvent.date.year
    @RequiresApi(Build.VERSION_CODES.O)
    var newMonth = pickedEvent.date.monthValue
    @RequiresApi(Build.VERSION_CODES.O)
    var newDay = pickedEvent.date.dayOfMonth
    @RequiresApi(Build.VERSION_CODES.O)
    var newHour = pickedEvent.date.hour
    @RequiresApi(Build.VERSION_CODES.O)
    var newMinute = pickedEvent.date.minute
    lateinit var date: TextView
    lateinit var year: TextView
    lateinit var time: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.event_information_layout, null)
        builder.setView(view)

        val cancelBtn: Button = view.findViewById(R.id.cancel_button)
        val editBtn: Button = view.findViewById(R.id.edit_button)
        val deleteBtn: Button = view.findViewById(R.id.delete_button)
        val dateContent: ConstraintLayout = view.findViewById(R.id.date_content)
        val title: EditText = view.findViewById(R.id.title)
        val description: EditText = view.findViewById(R.id.description)
        date = view.findViewById(R.id.day_date)
        year = view.findViewById(R.id.year)
        time = view.findViewById(R.id.time)

        date.text = "${pickedEvent.date.dayOfMonth } ${pickedEvent.date.month}"
        year.text = pickedEvent.date.year.toString()
        if(pickedEvent.date.minute.toString().length == 1){
            time.text = "${pickedEvent.date.hour}:${pickedEvent.date.minute}0"
        }
        else
            time.text = "${pickedEvent.date.hour}:${pickedEvent.date.minute}"


        val selectedDate: LocalDateTime = pickedEvent.date
        dateContent.setOnClickListener(){
            DatePickerDialog(this.context!!,this, selectedDate.year,
                      selectedDate.monthValue - 1, selectedDate.dayOfMonth).show()
        }

        cancelBtn.setOnClickListener(){
           this.dismiss()
        }

        dateContent.isClickable =false
        title.setText(pickedEvent.title)
        description.setText(pickedEvent.description)

        title.isEnabled = false
        description. isEnabled = false


        editBtn.setOnClickListener(){
            editModeOn(dateContent, title, description, it as Button, deleteBtn)
        }

        return builder.create()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editModeOn(dateContent: ConstraintLayout, title: EditText, description: EditText, editBtn: Button, deleteBtn: Button){
        dateContent.isClickable = true
        title.isEnabled = true
        description.isEnabled = true
        deleteBtn.visibility = View.VISIBLE
        editBtn.text = "Done"
        editBtn.setOnClickListener(){
            editModeOff(dateContent, title, description, it as Button, deleteBtn)

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editModeOff(dateContent: ConstraintLayout, title: EditText, description: EditText, editBtn: Button, deleteBtn: Button){
        if(title.text.toString() == "" || description.text.toString() =="") {
            Toast.makeText(this.context,"You cant save event without title or description", Toast.LENGTH_LONG).show()
            editModeOn(dateContent, title, description, editBtn as Button, deleteBtn)
            return
        }
        dateContent.isClickable = false
        title.isEnabled = false
        description.isEnabled = false
        deleteBtn.visibility = View.INVISIBLE
        editBtn.text = "Edit"
        pickedEvent.date = LocalDateTime.parse("$newYear-${newMonth+1}-${newDay}T${time.text}")
        pickedEvent.title = title.text.toString()
        pickedEvent.description = description.text.toString()

        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
        (recyclerView.adapter as RecyclerViewAdapter).notifyItemChanged(position)
        editBtn.setOnClickListener(){
            editModeOn(dateContent, title, description, it as Button, deleteBtn)
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        newDay = dayOfMonth
        newMonth = month
        newYear = year


        TimePickerDialog(this.context!!,this, pickedEvent.date.hour, pickedEvent.date.minute, true).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        newHour = hourOfDay
        newMinute = minute


        date.text = "$newDay ${getMonthName(newMonth)}"
        year.text = "$newYear"
        if(newMinute.toString().length == 1)
            time.text = "${newHour}:${newMinute}0"
        else
            time.text = "${newHour}:${newMinute}"

    }
}