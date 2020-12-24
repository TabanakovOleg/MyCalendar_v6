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
import java.util.*

class NewEventDialog: AppCompatDialogFragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private lateinit var title:EditText
    private lateinit var descripton:EditText
    private lateinit var date: TextView
    private lateinit var time: TextView
    private lateinit var button: Button




    var day = 0
    var month = 0
    var year = 0
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
                .setTitle("Triangle")
                .setNegativeButton(
                        "cancel"
                ) { dialogInterface, i -> }
                .setPositiveButton(
                        "ok"
                ) { dialogInterface, i ->
                    val eventTitle: String = title.text.toString()
                    val eventDescription: String = descripton.text.toString()
                    
                    val eventDate: LocalDateTime = LocalDateTime.parse("${savedYear}-${savedMonth}-${savedDay}T${savedHour}:${savedMinute}:00")
                    listener.applyNewEvent(eventDate, eventTitle,eventDescription)

                }

        button = view.findViewById(R.id.date_picker)
        button.setOnClickListener{
            getDateTimeCalendar()

            DatePickerDialog(this.context!!,this, year, month, day).show()
        }
        title = view.findViewById(R.id.title)
        descripton = view.findViewById(R.id.description)
        date = view.findViewById(R.id.picked_date)
        time = view.findViewById(R.id.picked_time)

        return  builder.create()
    }

    interface NewEventDialogListener{
        fun applyNewEvent(date: LocalDateTime, title: String, description: String)
    }

    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month= cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
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


        date.text = "Date: $savedDay/${savedMonth+1}/$savedYear"
        time.text = "Begin at: " + savedHour +":" + savedMinute
    }


}
