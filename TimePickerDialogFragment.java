package com.example.trainer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment {


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return  new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getContext(), hour, minute,true);

    }
}
