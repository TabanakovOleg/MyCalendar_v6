package com.example.myapplication1

import Event
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.CalendarView.OnDateChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.LocalDateTime


lateinit var recyclerView: RecyclerView

class MainActivity : AppCompatActivity(), NewEventDialog.NewEventDialogListener, RecyclerViewAdapter.OnItemClickListener {

    lateinit var calendarView: CalendarView
    var ourList = arrayListOf<Event>()

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
                var eventDateString: String = "Events of the $dayOfMonth"
                if ((4 <= dayOfMonth && dayOfMonth <= 20) || (24 <= dayOfMonth && dayOfMonth <= 30))
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
        val newEventDialog = NewEventDialog()
        newEventDialog.show(supportFragmentManager, "New event")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun applyNewEvent(date: LocalDateTime, title: String, description: String) {
        if(title != "" && description != "") {
            try {
                val newEvent = Event(date, title, description)
                Toast.makeText(this, newEvent.toString(), Toast.LENGTH_SHORT).show()
                allEvents.add(newEvent)
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
        var listOfFutureEvents = mutableListOf<Event>()
        allEvents.forEach{ if(it.date > LocalDateTime.now()){
            listOfFutureEvents.add(it)
        } }
        (recyclerView.adapter as RecyclerViewAdapter).updateList(listOfFutureEvents as ArrayList<Event>)
        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
        var title: TextView = findViewById(R.id.textView)
        title.text = "Future events"
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this,"Item $position clicked", Toast.LENGTH_LONG).show()
    }
}