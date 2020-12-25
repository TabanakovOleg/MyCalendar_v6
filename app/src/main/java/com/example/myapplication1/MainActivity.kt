package com.example.myapplication1

import Event
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

var currentYear = 0
var currentMonth = 0
var currentDay = 0


lateinit var recyclerView: RecyclerView

class MainActivity : AppCompatActivity(), NewEventDialog.NewEventDialogListener, RecyclerViewAdapter.OnItemClickListener {

    lateinit var calendarView: CalendarView
    var ourList = arrayListOf<Event>()

    @RequiresApi(Build.VERSION_CODES.O)
    var selectedDate = LocalDateTime.now()
    var showingFutureEvents: Boolean = true

    @RequiresApi(Build.VERSION_CODES.O)
    val e1 = Event(LocalDateTime.parse("2020-12-23T11:00:00"), "Event of 23.1", "ooo")
    @RequiresApi(Build.VERSION_CODES.O)
    val e2 = Event(LocalDateTime.parse("2020-12-23T13:00:00"), "Event of 23.2", "ooo")
    @RequiresApi(Build.VERSION_CODES.O)
    val e3 = Event(LocalDateTime.parse("2020-12-24T13:00:00"), "Event of 24.1", "ooo")

    val list23 = arrayListOf(e1,e2)
    val list24 = arrayListOf(e3)
    val allEvents = sortedSetOf(e1,e3,e2)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = RecyclerViewAdapter(list24, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        calendarView = findViewById(R.id.calendarView)
        showFutureEvents()

        calendarView.setOnDateChangeListener(object : OnDateChangeListener {
            override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
                currentYear = year
                currentMonth = month
                currentDay = dayOfMonth
                var eventDateString: String = "Events of the $dayOfMonth"
                if ((dayOfMonth in 4..20) || (dayOfMonth in 24..30))
                    eventDateString += "th"
                else if (dayOfMonth % 10 == 1) eventDateString += "st"
                else if (dayOfMonth % 10 == 2) eventDateString += "nd"
                else if (dayOfMonth % 10 == 3) eventDateString += "rd"
                eventDateString += " of " + getMonthName(month)
                val title: TextView = findViewById(R.id.textView)
                title.text = eventDateString
                ourList.clear()
                allEvents.forEach{ if(it.date.year == year && it.date.monthValue - 1 == month && it.date.dayOfMonth == dayOfMonth){
                    ourList.add(it)
                } }
                (recyclerView.adapter as RecyclerViewAdapter).updateList(ourList)

                (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                showingFutureEvents = false

                var selectedDateString = "$year-"
                if (month + 1 < 10) selectedDateString += "0"
                selectedDateString += "${month + 1}-"
                if (dayOfMonth < 10) selectedDateString += "0"
                selectedDateString += "${dayOfMonth}T12:00:00"
                selectedDate = LocalDateTime.parse(selectedDateString)
            }
        })

        var fab_add: FloatingActionButton = findViewById(R.id.fab_add)
        fab_add.setOnClickListener{
            openDialog()
        }

        var showFutureEvents: Button = findViewById(R.id.show_all_events)
        showFutureEvents.setOnClickListener{
            showFutureEvents()
        }
    }

    fun openDialog(){
        val newEventDialog = NewEventDialog(selectedDate)
        newEventDialog.show(supportFragmentManager, "New event")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openEventInformationDialog(pickedEvent: Event, position: Int){
        val eventInformationDialog = EventInformationDialog(pickedEvent, position)
        eventInformationDialog.show(supportFragmentManager, "Event information dialog")


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun applyNewEvent(date: LocalDateTime, title: String, description: String) {
        if(title != "" && description != "") {
            try {
                val newEvent = Event(date, title, description)
                Toast.makeText(this, newEvent.toString(), Toast.LENGTH_SHORT).show()
                allEvents.add(newEvent)

                if (showingFutureEvents) {
                    val futureEventList = arrayListOf<Event>()
                    for (event in allEvents) {
                        if (event.date > LocalDateTime.now()) {
                            futureEventList.add(event)
                        }
                    }

                    (recyclerView.adapter as RecyclerViewAdapter).updateList(futureEventList)
                    (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                }
                else if (selectedDate.year == newEvent.date.year && (selectedDate.monthValue == newEvent.date.monthValue) && (selectedDate.dayOfMonth == newEvent.date.dayOfMonth)) {
                    val selectedDateEventList = arrayListOf<Event>()
                    for (event in allEvents) {
                        if (event.date.year == selectedDate.year && event.date.monthValue == selectedDate.monthValue && event.date.dayOfMonth == selectedDate.dayOfMonth) {
                            selectedDateEventList.add(event)
                        }
                    }

                    (recyclerView.adapter as RecyclerViewAdapter).updateList(selectedDateEventList)
                    (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                }

                (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                recyclerView.adapter?.notifyItemInserted(0)

            } catch (e: Exception) {
                Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            }
        }
        else
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showFutureEvents(){
        val title: TextView = findViewById(R.id.textView)
        title.text = "Future events"

        val listOfFutureEvents = mutableListOf<Event>()
        allEvents.forEach{ if(it.date > LocalDateTime.now()){
            listOfFutureEvents.add(it)
        } }
        (recyclerView.adapter as RecyclerViewAdapter).updateList(listOfFutureEvents as ArrayList<Event>)
        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
        showingFutureEvents = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int) {

        openEventInformationDialog((recyclerView.adapter as RecyclerViewAdapter).getItem(position), position)


    }
}