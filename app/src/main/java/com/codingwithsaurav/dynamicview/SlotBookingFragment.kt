package com.codingwithsaurav.dynamicview

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.codingwithsaurav.dynamicview.databinding.FragmentSlotBookingBinding
import com.codingwithsaurav.dynamicview.databinding.RowAddScheduleBinding
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Locale


class SlotBookingFragment : Fragment() {

    private var scheduledList: ArrayList<Schedule> = ArrayList()
    private var durationList: MutableList<String?> = ArrayList()
    private var _binding: FragmentSlotBookingBinding? = null
    private val binding: FragmentSlotBookingBinding
        get() = requireNotNull(_binding)
    private var calendar: Calendar?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlotBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendar = Calendar.getInstance()
        initializeClick()
        binding?.buttonSubmitList?.setOnClickListener {
            if (checkIfValidAndRead()) {
                Toast.makeText(requireContext(), "Data Summited Successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeClick() {
        binding.apply {
            buttonAddMonday.setOnClickListener {
                addViewLayout(buttonAddMonday, scheduleMondayList, MONDAY)
            }
            buttonAddTuesday.setOnClickListener {
                addViewLayout(buttonAddTuesday, scheduleTuesdayList, TUESDAY)
            }
            buttonAddWednesday.setOnClickListener {
                addViewLayout(buttonAddWednesday, scheduleWednesdayList, WEDNESDAY)
            }
            buttonAddThursday.setOnClickListener {
                addViewLayout(buttonAddThursday, scheduleThursdayList, THURSDAY)
            }
            buttonAddFirday.setOnClickListener {
                addViewLayout(buttonAddFirday, scheduleFridayList, FRIDAY)
            }
            buttonAddSaturday.setOnClickListener {
                addViewLayout(buttonAddSaturday, scheduleSaturdayList, SATURDAY)
            }
            buttonAddSunday.setOnClickListener {
                addViewLayout(buttonAddSunday, scheduleSundayList, SUNDAY)
            }
        }
        durationList.add("15 min")
        durationList.add("30 min")
        durationList.add("45 min")
        durationList.add("60 min")
        durationList.add("75 min")
    }

    private fun addViewLayout(clickedView: Button, scheduleLayoutList: LinearLayout, scheduleDay: String) {
        clickedView.isVisible = false
        scheduleLayoutList.isVisible = true
        addView(scheduleLayoutList, scheduleDay)
    }

    private fun getValidate(linearLayout: LinearLayout): Boolean {
        for (index in 0 until linearLayout.childCount){
            val binding = DataBindingUtil.getBinding<RowAddScheduleBinding>(linearLayout.getChildAt(index))
            binding?.apply {
                val isValid = when {
                    editTextStartTimeLayout.isError() || editTextEndTimeLayout.isError() || editTextSpinnerLayout.isError() -> {
                        false
                    }
                    editTextStartTime.isEmpty() -> {
                        editTextStartTimeLayout.error = "Start time should not be empty"
                        false
                    }
                    editTextEndTime.isEmpty() -> {
                        editTextEndTimeLayout.error = "End time should not be empty"
                        false
                    }
                    spinnerDuration.isEmpty() -> {
                        editTextSpinnerLayout.error = "Please select slot duration"
                        false
                    }
                    else -> {
                        true
                    }
                }
                if(isValid.not()){
                    return false
                }
            }
        }
        return true
    }

    private fun isStartTimeValid(linearLayout: LinearLayout): Boolean {
        for (index in 0 until linearLayout.childCount){
            val binding = DataBindingUtil.getBinding<RowAddScheduleBinding>(linearLayout.getChildAt(index))
            binding?.apply {
                val isStartTimeIsValid = when {
                    isStartTimeIsLessThenPreviousEndTime(linearLayout, index) -> {
                        editTextStartTimeLayout.error = "Start time should be greater than previous end time"
                        false
                    }
                    editTextEndTime.isNotEmpty() && editTextStartTime.isNotEmpty() &&
                        editTextStartTime.getTimeInMinute() >= editTextEndTime.getTimeInMinute() -> {
                        editTextStartTimeLayout.error = null
                        editTextEndTimeLayout.error = "End time should be greater than start time"
                         false
                    }
                    editTextStartTime.isNotEmpty() && editTextEndTime.isNotEmpty() && spinnerDuration.isNotEmpty() &&
                            (editTextEndTime.getTimeInMinute() - editTextStartTime.getTimeInMinute()) < spinnerDuration.getDurationInMinute() -> {
                        editTextEndTimeLayout.error = "Please select a suitable end time"
                        false
                    }
                    else -> {
                        editTextStartTimeLayout.error = null
                        editTextEndTimeLayout.error = null
                        spinnerDuration.error = null
                        true
                    }
                }
                if(isStartTimeIsValid.not()){
                    return false
                }
            }
        }
        return true
    }

    private fun isEndTimeValid(linearLayout: LinearLayout): Boolean {
        for (index in 0 until linearLayout.childCount) {
            val binding = DataBindingUtil.getBinding<RowAddScheduleBinding>(linearLayout.getChildAt(index))
            binding?.apply {
                val isEndTimeIsValid = when {
                    editTextStartTime.isNotEmpty() && editTextEndTime.isNotEmpty() &&
                            editTextStartTime.getTimeInMinute() >= editTextEndTime.getTimeInMinute() ->{
                        editTextEndTimeLayout.error = "End time should be greater than start time"
                        false
                    }
                    editTextStartTime.isNotEmpty() && editTextEndTime.isNotEmpty() && spinnerDuration.isNotEmpty() &&
                            (editTextEndTime.getTimeInMinute() - editTextStartTime.getTimeInMinute()) < spinnerDuration.getDurationInMinute() -> {
                        editTextEndTimeLayout.error = "Please select a suitable end time"
                        isEndTimeGreaterThenNextStartTime(linearLayout, index)
                        false
                    }
                    isEndTimeGreaterThenNextStartTime(linearLayout, index) -> {
                        editTextEndTimeLayout.error = null
                        false
                    }
                    else -> {
                        spinnerDuration.error = null
                        editTextEndTimeLayout.error = null
                        true
                    }
                }
                if(isEndTimeIsValid.not()){
                    return false
                }
            }
        }
        return true
    }

    private fun isDurationIsSuitable(linearLayout: LinearLayout): Boolean {
        for (index in 0 until linearLayout.childCount) {
            val binding = DataBindingUtil.getBinding<RowAddScheduleBinding>(linearLayout.getChildAt(index))
            binding?.apply {
                val isDurationValid = when {
                    editTextStartTime.isNotEmpty() && editTextEndTime.isNotEmpty() && spinnerDuration.isNotEmpty() &&
                            (editTextEndTime.getTimeInMinute() - editTextStartTime.getTimeInMinute()) < spinnerDuration.getDurationInMinute() -> {
                        editTextEndTimeLayout.error = "Please select a suitable end time"
                        editTextSpinnerLayout.error = null
                        false
                    }
                    else -> {
                        editTextSpinnerLayout.error = null
                        if(editTextEndTimeLayout.error == "Please select a suitable end time"){
                            editTextEndTimeLayout.error = null
                        }
                        true
                    }
                }
                if(isDurationValid.not()){
                    return isDurationValid
                }
            }
        }
        return true
    }

    private fun isStartTimeIsLessThenPreviousEndTime(scheduleLayoutList: LinearLayout, index: Int):Boolean {
        if (scheduleLayoutList.childCount >= 1) {
            DataBindingUtil.getBinding<RowAddScheduleBinding>(scheduleLayoutList.getChildAt(index))?.takeIf {
                it.editTextStartTime.isNotEmpty() }?.let { currentLayout->
                DataBindingUtil.getBinding<RowAddScheduleBinding>(scheduleLayoutList.getChildAt(index-1))?.takeIf {
                    it.editTextEndTime.isNotEmpty() }?.let { previousLayout->
                    return currentLayout.editTextStartTime.getTimeInMinute() <=  previousLayout.editTextEndTime.getTimeInMinute()
                }
            }
        }
        return false
    }

    private fun isEndTimeGreaterThenNextStartTime(scheduleLayoutList: LinearLayout, currentIndex: Int):Boolean{
        if(scheduleLayoutList.childCount >= currentIndex+1){
            DataBindingUtil.getBinding<RowAddScheduleBinding>(scheduleLayoutList.getChildAt(currentIndex))?.takeIf {
                it.editTextEndTime.isNotEmpty() }?.let { currentLayout ->

                DataBindingUtil.getBinding<RowAddScheduleBinding>(scheduleLayoutList.getChildAt(currentIndex+1))?.takeIf {
                    it.editTextStartTime.isNotEmpty() }?.let {nextLayout->
                    Log.d("snlfndslfnlsdkfnl", nextLayout.editTextStartTimeLayout.error.toString())
                    if(currentLayout.editTextEndTime.getTimeInMinute() >= nextLayout.editTextStartTime.getTimeInMinute()){
                        nextLayout.editTextStartTimeLayout.error = "Start time should be greater then previous end time"
                        Log.d("snlfndslfnlsdkfnl true", nextLayout.editTextStartTimeLayout.error.toString())
                        return true
                    }else{
                        Log.d("snlfndslfnlsdkfnl false", nextLayout.editTextStartTimeLayout.error.toString())
                        nextLayout.editTextStartTimeLayout.error = null
                    }
                }
            }
        }
        return false
    }

    private fun addView(scheduleLayoutList: LinearLayout, scheduledDay: String) {
        if (getValidate(scheduleLayoutList).not()) {
            return
        }
        val scheduleView: RowAddScheduleBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_add_schedule, null, false)
        scheduleView.apply {
            editTextStartTimeLayout.errorIconDrawable = null
            editTextEndTimeLayout.errorIconDrawable = null
            editTextSpinnerLayout.errorIconDrawable = null
            spinnerDuration.setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item, durationList))
            editTextStartTime.setOnClickListener { showTimePickerDialog(editTextStartTime, scheduleLayoutList,   true) }
            editTextEndTime.setOnClickListener { showTimePickerDialog(editTextEndTime, scheduleLayoutList, false) }

            spinnerDuration.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                isDurationIsSuitable(scheduleLayoutList)
            }

            buttonAdd.setOnClickListener {
                addView(scheduleLayoutList, scheduledDay)
            }
            buttonDelete.setOnClickListener {
                removeView(scheduleLayoutList, scheduleView, scheduledDay)
            }
        }
        scheduleLayoutList.addView(scheduleView.root)
        updateAddButtonVisibility(scheduleLayoutList)
    }

    private fun updateAddButtonVisibility(scheduleLayoutList: LinearLayout) {
        for (i in 0 until scheduleLayoutList.childCount) {
            val binding = DataBindingUtil.getBinding<ViewDataBinding>(scheduleLayoutList.getChildAt(i))
            binding?.apply {
                if (this is RowAddScheduleBinding) {
                    buttonAdd.isVisible = i == scheduleLayoutList.childCount - 1
                }
            }
        }
    }

    private fun showTimePickerDialog(editTextTime: TextInputEditText, scheduleLayoutList: LinearLayout, isStartDate:Boolean) {
        calendar?.let {
            TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                editTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
                if(isStartDate) isStartTimeValid(scheduleLayoutList) else isEndTimeValid(scheduleLayoutList)},
                it.get(Calendar.HOUR_OF_DAY),
                it.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun removeView(scheduleLayoutList: LinearLayout, scheduleLayout: RowAddScheduleBinding, scheduledDay: String) {
        scheduleLayoutList.removeView(scheduleLayout.root)
        if (scheduleLayoutList.childCount == 0) {
            val addButton = when (scheduledDay) {
                MONDAY -> binding.buttonAddMonday
                TUESDAY -> binding.buttonAddTuesday
                WEDNESDAY -> binding.buttonAddWednesday
                THURSDAY -> binding.buttonAddThursday
                FRIDAY -> binding.buttonAddFirday
                SATURDAY -> binding.buttonAddSaturday
                SUNDAY -> binding.buttonAddSunday
                else -> return
            }
            addButton.isVisible = true
        } else {
            updateAddButtonVisibility(scheduleLayoutList)
        }
    }

    private fun checkIfValidAndRead(): Boolean {
        binding.apply {
           return getValidate(scheduleMondayList) &&
            getValidate(scheduleTuesdayList) &&
            getValidate(scheduleWednesdayList) &&
            getValidate(scheduleThursdayList) &&
            getValidate(scheduleFridayList) &&
            getValidate(scheduleSaturdayList) &&
            getValidate(scheduleSundayList)
        }
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
