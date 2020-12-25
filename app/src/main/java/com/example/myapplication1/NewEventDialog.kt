package com.example.myapplication1

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class NewEventDialog(val selectedDate: LocalDateTime): AppCompatDialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private lateinit var title:EditText
    private lateinit var descripton:EditText
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var button: Button

    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    lateinit var listener: NewEventDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as NewEventDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                    context.toString() +
                            "must implement ExampleDialogListener"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.new_event_dialog, null)
        builder.setView(view)
                .setTitle("Create new Event")
                .setNegativeButton(
                        "cancel"
                ) { dialogInterface, i -> }
                .setPositiveButton(
                        "ok"
                ) { dialogInterface, i ->
                    val eventTitle: String = title.text.toString()
                    val eventDescription: String = descripton.text.toString()

                    var eventDateString: String
                    if (savedYear == 0) {
                        eventDateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        eventDateString += "T10:00:00"
                    } else {
                        eventDateString = "$savedYear-"
                        savedMonth += 1
                        if (savedMonth < 10) eventDateString += "0"
                        eventDateString += "$savedMonth-"
                        if (savedDay < 10) eventDateString += "0"
                        eventDateString += "${savedDay}T"
                        if (savedHour < 10) eventDateString += "0"
                        eventDateString += "$savedHour:"
                        if (savedMinute < 10) eventDateString += "0"
                        eventDateString += "$savedMinute:00"
                    }

                    val eventDate: LocalDateTime = LocalDateTime.parse(eventDateString)
                    listener.applyNewEvent(eventDate, eventTitle,eventDescription)
                }

        button = view.findViewById(R.id.date_picker)
        button.setOnClickListener{
            DatePickerDialog(this.context!!,this, selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth).show()
        }
        title = view.findViewById(R.id.title)
        descripton = view.findViewById(R.id.description)
        date = view.findViewById(R.id.picked_date)
        time = view.findViewById(R.id.picked_time)

        val dateText: String = selectedDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        date.text = dateText

        return  builder.create()
    }

    interface NewEventDialogListener{
        fun applyNewEvent(date: LocalDateTime, title: String, description: String)
    }

    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()

        TimePickerDialog(this.context!!,this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        var dateText = "Date: $savedYear/"
        if (savedMonth < 9) dateText += "0"
        dateText += "${savedMonth + 1}/"
        if (savedDay < 10) dateText += "0"
        dateText += savedDay
        date.text = dateText


        var timeText = "Begin at: "
        if (savedHour < 10) timeText += "0"
        timeText += "$savedHour:"
        if (savedMinute < 10) timeText += "0"
        timeText += "$savedMinute"
        time.text = timeText
    }
}