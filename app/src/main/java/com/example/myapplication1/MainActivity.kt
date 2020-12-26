package com.example.myapplication1

import Event
import android.annotation.SuppressLint
import android.content.Context
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
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.util.*

var currentYear = 0
var currentMonth = 0
var currentDay = 0

lateinit var recyclerView: RecyclerView
// Список, в который будут собираться события, «принадлежащие» выбранной дате
val displayList = arrayListOf<Event>()

class MainActivity : AppCompatActivity(),
                     NewEventDialog.NewEventDialogListener,
                     RecyclerViewAdapter.OnItemClickListener {

    // Виджет, оторбражющий календарь
    lateinit var calendarView: CalendarView

    // allEvents -- остортированный по дате набор всех событий
    var allEvents = mutableSetOf<Event>()
    /* Костыли
     * selectedDate -- значение выбранной в данный момент на календаре даты
     * displayingFutureEvent -- булева переменная, означающая, содержатся ли
     */
    @RequiresApi(Build.VERSION_CODES.O)
    var selectedDate = LocalDateTime.now()
    var displayingFutureEvents: Boolean = true

    // Метод, вызываемый при создании объекта
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Панель, отображающая список событий
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = RecyclerViewAdapter(arrayListOf(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        calendarView = findViewById(R.id.calendarView)


        /*---------------------------------------------------------------//

        Заполнение сета ивентов из файла, если таковой имеется.
        -Можно вынести в отдельные функции, чтобы не засорять main()

        //---------------------------------------------------------------*/
        var jstring: String
        if(fileExist("1.json")) {
            jstring = load("1.json")

            val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter()).create()
            val eventSortedSetTypeToken = object : TypeToken<MutableSet<Event>>() {}.type

            try {
               allEvents = gson.fromJson(jstring, eventSortedSetTypeToken)
            } catch (e: Exception) {
                mutableSetOf<Event>()
            }
        }

        showFutureEvents()



        calendarView.setOnDateChangeListener(object : OnDateChangeListener {
            // Функция, вызываемая при изменении выбранной пользователем даты
            override fun onSelectedDayChange(view: CalendarView,
                                             year: Int, month: Int, dayOfMonth: Int) {
                currentYear = year
                currentMonth = month
                currentDay = dayOfMonth

                /* Сборка строки (eventDateString), которая будет отображать текущую выбранную дату
                   над списком событий с добавлением погрядковых суффиксов */
                var eventDateString: String = "Events of the $dayOfMonth"
                if ((dayOfMonth in 4..20) || (dayOfMonth in 24..30))
                    eventDateString += "th"
                else if (dayOfMonth % 10 == 1) eventDateString += "st"
                else if (dayOfMonth % 10 == 2) eventDateString += "nd"
                else if (dayOfMonth % 10 == 3) eventDateString += "rd"
                eventDateString += " of " + getMonthName(month)

                // Элемент, который отображает текст над списком событий
                val title: TextView = findViewById(R.id.textView)
                title.text = eventDateString
                displayList.clear()

                for (event in allEvents.toSortedSet()) {
                    if ((event.date.year       == year) &&
                        (event.date.monthValue == month + 1) &&
                        (event.date.dayOfMonth == dayOfMonth)) {
                        displayList.add(event)
                    }
                }

                // Обновлениее отображаемого списка
                (recyclerView.adapter as RecyclerViewAdapter).updateList(displayList)
                (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()

                displayingFutureEvents = false // будущие события больше не отображаются

                // Хранение значения текущей выбранной даты в переменной selectedDate
                selectedDate = LocalDateTime.parse(buildDateString(year, month + 1, dayOfMonth))
            }
        })

        val fab_add: FloatingActionButton = findViewById(R.id.fab_add)
        fab_add.setOnClickListener{
            openDialog()
        }

        val showFutureEvents: FloatingActionButton = findViewById(R.id.fab_future_event)
        showFutureEvents.setOnClickListener{
            showFutureEvents()
        }
    }


    fun fileExist(fname: String?): Boolean {
        val file = baseContext.getFileStreamPath(fname)
        return file.exists()
    }

    fun openDialog(){
        val newEventDialog = NewEventDialog(selectedDate)
        newEventDialog.show(supportFragmentManager, "New event")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun openEventInformationDialog(pickedEvent: Event, position: Int){
        val eventInformationDialog = EventInformationDialog(pickedEvent, position, displayList, allEvents, displayingFutureEvents)
        eventInformationDialog.show(supportFragmentManager, "Event information dialog")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun applyNewEvent(date: LocalDateTime, title: String, description: String) {
        if(title != "" && description != "") {
            try {
                val newEvent = Event(date, title, description)
                Toast.makeText(this, newEvent.toString(), Toast.LENGTH_SHORT).show()

                allEvents.add(newEvent)

                /* Обновление отображаемого списка */
                // Случай, когда мы доавили будущее событие, а сейчас отображается список будущих
                if (displayingFutureEvents && newEvent.date > LocalDateTime.now()) {
                    val futureEventList = arrayListOf<Event>()
                    for (event in allEvents.toSortedSet()) {
                        if (event.date > LocalDateTime.now()) {
                            futureEventList.add(event)
                        }
                    }

                    (recyclerView.adapter as RecyclerViewAdapter).updateList(futureEventList)
                    (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                }
                // Случай, когда мы добавили событие в отображаемый сейчас день
                else if (areSameDays(selectedDate, newEvent.date)) {
                    val selectedDateEventList = arrayListOf<Event>()
                    for (event in allEvents.toSortedSet()) {
                        if (areSameDays(selectedDate, event.date)) {
                            selectedDateEventList.add(event)
                        }
                    }

                    (recyclerView.adapter as RecyclerViewAdapter).updateList(selectedDateEventList)
                    (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                }

                (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
                recyclerView.adapter?.notifyItemInserted(0)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        else
            Toast.makeText(this, "Title and description can't be empty",
                           Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun showFutureEvents(){
        val title: TextView = findViewById(R.id.textView)
        title.text = "Future text"

        val futureEventList = arrayListOf<Event>()
        for (event in allEvents.toSortedSet()) {
            if (event.date > LocalDateTime.now()) futureEventList.add(event)
        }

        (recyclerView.adapter as RecyclerViewAdapter).updateList(futureEventList)
        (recyclerView.adapter as RecyclerViewAdapter).notifyDataSetChanged()
        displayingFutureEvents = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(position: Int) {
        openEventInformationDialog((recyclerView.adapter as RecyclerViewAdapter).getItem(position),
                                    position)
    }




    //--------Сохранение в файл при закрытии--------//


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        if(isFinishing()){
            val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter()).create()
            val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type
            val eventsInJson = gson.toJson(allEvents, eventSortedSetTypeToken)
            save("1.json",eventsInJson)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter()).create()
        val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type
        val eventsInJson = gson.toJson(allEvents, eventSortedSetTypeToken)
        save("1.json",eventsInJson)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter()).create()
        val eventSortedSetTypeToken = object: TypeToken<SortedSet<Event>>() {}.type
        val eventsInJson = gson.toJson(allEvents, eventSortedSetTypeToken)
        save("1.json",eventsInJson)
    }

    //--------------------------------------------//

    fun save(fileName: String,fileData: String) {
        val file:String = fileName
        val data:String = fileData
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(file, Context.MODE_PRIVATE)
            fileOutputStream.write(data.toByteArray())
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun load(filename: String): String {
        var fileInputStream: FileInputStream? = null
        fileInputStream = openFileInput(filename)
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()
        var text: String? = ""
        text = bufferedReader.readLine();
        return text.toString()
    }
}