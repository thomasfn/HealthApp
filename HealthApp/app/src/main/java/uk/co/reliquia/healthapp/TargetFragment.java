package uk.co.reliquia.healthapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

/*
 * TargetFragment - The target calculator fragment
 */
public class TargetFragment extends Fragment
{
    // The argument name for the section number
    private static final String ARG_SECTION_NUMBER = "section_number";

    // The title of the page that this fragment will sit in
    public static final String PAGE_TITLE = "Target";

    private NumberPicker DaysNumberPicker;
    private EditText WeightEditText;
    private Spinner WeightUnitSpinner;

    private TextView GainLoseTextView, TargetTextView, AdviceTextView;

    private SharedPreferences prefs;

    private ArrayAdapter<CharSequence> weightunitadapter;
    private CharSequence currentweightunit;

    private boolean ignore_changes;

    /*
     * createInstance - Creates a new instance of this fragment for the given section number
     */
    public static TargetFragment createInstance(int sectionNumber)
    {
        // Initialise it
        TargetFragment fragment = new TargetFragment();

        // Setup the args
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        // Return it
        return fragment;
    }

    /*
     * TargetFragment - Default constructor
     */
    public TargetFragment()
    {
    }

    /*
     * onCreateView - Called when the view for this fragment is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create the fragment view
        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);

        // Get shared prefs
        prefs = AppContext.getSharedPreferences();

        // Get widgets we need to access
        WeightUnitSpinner = (Spinner)rootView.findViewById(R.id.WeightUnitSpinner);
        WeightEditText = (EditText) rootView.findViewById(R.id.WeightEditText);
        DaysNumberPicker = (NumberPicker) rootView.findViewById(R.id.DaysNumberPicker);
        GainLoseTextView = (TextView) rootView.findViewById(R.id.GainLoseTextView);
        TargetTextView = (TextView) rootView.findViewById(R.id.TargetTextView);
        AdviceTextView = (TextView) rootView.findViewById(R.id.AdviceTextView);

        // Set the weight units spinner adapter
        weightunitadapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.weight_units_array, android.R.layout.simple_spinner_dropdown_item);
        WeightUnitSpinner.setAdapter(weightunitadapter);

        // Set weight values from user prefs
        WeightUnitSpinner.setSelection(prefs.getInt("targetWeightUnit", 0));
        currentweightunit = weightunitadapter.getItem(WeightUnitSpinner.getSelectedItemPosition());
        WeightEditText.setText(prefs.getString("targetWeight", "75"));

        // Weight unit selection listener
        WeightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ignore_changes) return;
                CharSequence item = weightunitadapter.getItem(position);
                ignore_changes = true;
                setWeightUnit(item);
                ignore_changes = false;

                // Set new user prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("targetWeightUnit", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (ignore_changes) return;

            }
        });

        // Weight text box listener
        TextWatcher watcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ignore_changes) return;
                Calculate();
            }
        };
        WeightEditText.addTextChangedListener(watcher);

        // Return the view
        return rootView;
    }

    private static int kilogramsToPounds(int kg)
    {
        return (int)(kg * 2.20462f);
    }

    private static int poundsToKilograms(int kg)
    {
        return (int)(kg / 2.20462f);
    }

    private void setWeightUnit(CharSequence weightunit)
    {
        // Check if a change happened
        if (currentweightunit.equals(weightunit)) return;

        // Convert between the units
        if (currentweightunit.equals("Kilograms") && weightunit.equals("Pounds"))
        {
            // Convert from kg to pounds
            int kg = Integer.parseInt(WeightEditText.getText().toString());
            WeightEditText.setText(String.format("%d", kilogramsToPounds(kg)));
        }
        if (currentweightunit.equals("Pounds") && weightunit.equals("Kilograms"))
        {
            // Convert from pounds to kg
            int pounds = Integer.parseInt(WeightEditText.getText().toString());
            WeightEditText.setText(String.format("%d", poundsToKilograms(pounds)));
        }

        // Store
        currentweightunit = weightunit;
    }

    private void Calculate()
    {
        // Get target weight
        int targetWeightKG;
        try
        {
            int rawWeight = Integer.parseInt(WeightEditText.getText().toString());
            if (currentweightunit.equals("Pounds"))
            {
                // Convert from pounds to kg
                targetWeightKG = poundsToKilograms(rawWeight);
            }
            else
                targetWeightKG = rawWeight;
        }
        catch (Exception ex) { return; }

        // Calculate diff
        int diff = BMIFragment.TheWeightKG - targetWeightKG;

        // Set debug thing
        GainLoseTextView.setText(String.format("Thingy %d", diff));

    }
}
