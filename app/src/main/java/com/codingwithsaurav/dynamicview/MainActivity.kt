package com.codingwithsaurav.dynamicview

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.codingwithsaurav.dynamicview.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var teamList: MutableList<String?> = ArrayList()
    private var binding: ActivityMainBinding? = null

    private var scheduledList: ArrayList<Schedule> = ArrayList<Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initializeClick()
        binding?.buttonSubmitList?.setOnClickListener {
            if(checkIfValidAndRead()){
               /* for (i in 0 until binding?.scheduleMondayList!!.childCount) {
                    val cricketerView = layoutList!!.getChildAt(i)
                    val editTextStartTime = scheduleView.findViewById<View>(R.id.editTextStartTime) as EditText
                    val editTextEndTime = scheduleView.findViewById<View>(R.id.editTextEndTime) as EditText
                    val spinnerTeam = scheduleView.findViewById<View>(R.id.spinner_team) as AppCompatSpinner
                    val schedule = if (spinnerTeam.selectedItemPosition != 0) {
                        Schedule(scheduledDay, editTextStartTime.text.toString().toString(), editTextEndTime.text.toString().trim(), teamList[spinnerTeam.selectedItemPosition]!!)
                    } else {
                        Schedule(scheduledDay, editTextStartTime.text.toString().toString(), editTextEndTime.text.toString().trim(), "")
                    }
                    scheduledList.add(schedule)
                }*/
            }
        }
    }

    private fun checkIfValidAndRead(): Boolean {
        scheduledList.clear()
        var result = true
        binding?.scheduleMondayList?.let { it-> result = getValidate(it)  }
        binding?.scheduleTuesdayList?.let { it-> result = getValidate(it)}
        binding?.scheduleWednesdayList?.let { it-> result = getValidate(it) }
        binding?.scheduleThursdayList?.let { it-> result = getValidate(it)}
        binding?.scheduleFirdayList?.let { it-> result = getValidate(it) }
        binding?.scheduleSaturdayList?.let { it-> result = getValidate(it) }
        binding?.scheduleSundayList?.let { it-> result = getValidate(it) }
        return result
    }

    private fun getValidate(linearLayout: LinearLayout): Boolean {
        var result = true
        for (i in 0 until linearLayout.childCount) {
            val scheduleView = linearLayout.getChildAt(i)
            val editTextStartTime = scheduleView.findViewById<View>(R.id.editTextStartTime) as EditText
            val editTextEndTime = scheduleView.findViewById<View>(R.id.editTextEndTime) as EditText
            val spinnerTeam = scheduleView.findViewById<View>(R.id.spinner_team) as AppCompatSpinner
            val cricketer = Schedule()
            if (editTextStartTime.text.toString().trim().isNullOrEmpty()) {
                result = false
                editTextStartTime.error = "Start date should no be empty"
            }
            if (editTextEndTime.text.toString().trim().isNullOrEmpty()) {
                result = false
                editTextEndTime.error = "End date should no be empty"
            }
            if (spinnerTeam.selectedItemPosition == 0) {
                (spinnerTeam.getChildAt(0) as? TextView)?.error = "Please select a valid option"
                result = false
            }
        }
        return result
    }

    private fun addView(scheduleLayoutList: LinearLayout, scheduledDay: String) {
        val scheduleView: View = layoutInflater.inflate(R.layout.row_add_schedule, null, false)
        val editTextStartTime = scheduleView.findViewById<View>(R.id.editTextStartTime) as EditText
        val editTextEndTime = scheduleView.findViewById<View>(R.id.editTextEndTime) as EditText
        val spinnerTeam = scheduleView.findViewById<View>(R.id.spinner_team) as AppCompatSpinner
        val buttonDelete = scheduleView.findViewById<View>(R.id.buttonDelete) as TextView
        val buttonAdd = scheduleView.findViewById<View>(R.id.buttonAdd) as TextView
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, teamList as List<Any?>)
        spinnerTeam.adapter = arrayAdapter
        buttonAdd.tag = scheduledDay
        buttonDelete.tag = scheduledDay
        editTextStartTime.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(this@MainActivity, { _, hourOfDay, minute ->
                val selectedTime = "$hourOfDay:$minute"
                editTextStartTime.setText(selectedTime) }, hour, minute, DateFormat.is24HourFormat(this))
            timePickerDialog.show()
        }
        editTextEndTime.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(this@MainActivity, { _, hourOfDay, minute ->
                    val selectedTime = "$hourOfDay:$minute"
                editTextEndTime.setText(selectedTime) }, hour, minute, DateFormat.is24HourFormat(this))
            timePickerDialog.show()
        }
        buttonAdd.setOnClickListener {
            when (it.tag) {
                MONDAY -> { binding?.scheduleMondayList?.let { addView(it, MONDAY) } }
                TUESDAY -> { binding?.scheduleTuesdayList?.let { addView(it, TUESDAY) } }
                WEDNESDAY -> { binding?.scheduleWednesdayList?.let { addView(it, WEDNESDAY) } }
                THURSDAY -> { binding?.scheduleThursdayList?.let { addView(it, THURSDAY) } }
                FRIDAY -> { binding?.scheduleFirdayList?.let { addView(it, FRIDAY) } }
                SATURDAY -> { binding?.scheduleSaturdayList?.let { addView(it, SATURDAY) } }
                SUNDAY -> { binding?.scheduleSundayList?.let { addView(it, SATURDAY) } }
            }
        }
        buttonDelete.setOnClickListener {
            when (it.tag) {
                MONDAY -> { binding?.scheduleMondayList?.let { removeView(it, MONDAY, scheduleView) } }
                TUESDAY -> { binding?.scheduleTuesdayList?.let { removeView(it, TUESDAY, scheduleView) } }
                WEDNESDAY -> { binding?.scheduleWednesdayList?.let { removeView(it, WEDNESDAY, scheduleView) } }
                THURSDAY -> { binding?.scheduleThursdayList?.let { removeView(it, THURSDAY, scheduleView) } }
                FRIDAY -> { binding?.scheduleFirdayList?.let { removeView(it, FRIDAY, scheduleView) } }
                SATURDAY -> { binding?.scheduleSaturdayList?.let { removeView(it, SATURDAY, scheduleView) } }
                SUNDAY -> { binding?.scheduleSundayList?.let { removeView(it, SATURDAY, scheduleView) } }
            }
        }
        scheduleLayoutList?.addView(scheduleView)
        updateAddButtonVisibility(scheduleLayoutList)
    }

    private fun updateAddButtonVisibility(scheduleLayoutList: LinearLayout) {
        scheduleLayoutList.let { it ->
            for (i in 0 until it.childCount) {
                val scheduleView = it.getChildAt(i)
                (scheduleView.findViewById<View>(R.id.buttonAdd) as TextView).isVisible = i == it.childCount-1
            }
        }
    }

    private fun removeView(scheduleLayoutList: LinearLayout, scheduledDay: String, view: View) {
        scheduleLayoutList?.removeView(view)
       if(scheduleLayoutList.childCount == 0){
           when(scheduledDay){
               MONDAY -> { binding?.let { it.buttonAddMonday.isVisible = true } }
               TUESDAY -> { binding?.let { it.buttonAddTuesday.isVisible = true } }
               WEDNESDAY -> { binding?.let {it.buttonAddWednesday.isVisible = true } }
               THURSDAY -> { binding?.let { it.buttonAddThursday.isVisible = true } }
               FRIDAY -> { binding?.let { it.buttonAddFirday.isVisible = true } }
               SATURDAY -> { binding?.let { it.buttonAddSaturday.isVisible = true } }
               SUNDAY -> { binding?.let { it.buttonAddSunday.isVisible = true } }
           }
       }else{
          updateAddButtonVisibility(scheduleLayoutList)
       }
    }

    private fun initializeClick() {
        binding?.apply {
            buttonAddMonday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleMondayList, MONDAY)
            }
            buttonAddTuesday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleTuesdayList, TUESDAY)
            }
            buttonAddWednesday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleWednesdayList, WEDNESDAY)
            }
            buttonAddThursday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleThursdayList, THURSDAY)
            }
            buttonAddFirday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleFirdayList, FRIDAY)
            }
            buttonAddSaturday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleSaturdayList, SATURDAY)
            }
            buttonAddSunday.setOnClickListener {
                it.isVisible = false
                binding?.scheduleMondayList?.isVisible = true
                addView(binding!!.scheduleSundayList, SUNDAY)
            }
        }
        teamList.add("15 min")
        teamList.add("30 min")
        teamList.add("45 min")
        teamList.add("60 min")
    }

    companion object {
        const val MONDAY = "monday"
        const val TUESDAY = "tuesday"
        const val WEDNESDAY = "wednesday"
        const val THURSDAY = "thursday"
        const val FRIDAY = "Friday"
        const val SATURDAY = "saturday"
        const val SUNDAY = "sunday"
    }

}