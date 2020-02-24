package org.baole.oned

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.baole.oned.model.Story
import org.baole.oned.util.DateUtil
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var onKeySelected: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val key = arguments?.getString(Story.FIELD_DAY) ?: DateUtil.day2key()
        val date = DateUtil.key2date(key)

        val c = Calendar.getInstance()
        c.timeInMillis = date.time
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity!!, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onKeySelected?.invoke(DateUtil.day2key(year, month, day))
    }
}