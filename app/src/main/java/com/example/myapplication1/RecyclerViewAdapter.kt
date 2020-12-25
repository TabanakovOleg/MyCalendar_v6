package com.example.myapplication1

import Event
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private var exampleList: ArrayList<Event>,
                          private val listener: OnItemClickListener
                          ): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.example_item,
            parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount() = exampleList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = exampleList[position]

        holder.textVie1.text = currentItem.title
/*        holder.textVie2.text = currentItem.toString()*/


    }

    fun getItem(pos: Int): Event{
        return exampleList.get(pos)
    }

    fun deleteItem(pos:Int){
        exampleList.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun updateList(newList: ArrayList<Event>){
        exampleList = newList
    }


    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
    View.OnClickListener{
        val textVie1: TextView = itemView.findViewById(R.id.text_view_1)
        /*val textVie2: TextView = itemView.findViewById(R.id.text_view_2)*/

        init{

            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)


            /*val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                *//*itemView.setBackgroundColor(Color.BLUE)*//*
                *//*listener.onItemClick(position)*/
            }
        }
    }


    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}