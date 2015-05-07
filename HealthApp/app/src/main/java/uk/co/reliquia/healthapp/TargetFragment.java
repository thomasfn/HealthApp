package uk.co.reliquia.healthapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    private NumberPicker WeeksNumberPicker;
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
        View rootView = inflater.inflate(R.layout.fragment_target, container, false);

        // Get shared prefs
        prefs = AppContext.getSharedPreferences();

        // Get widgets we need to access
        WeightUnitSpinner = (Spinner)rootView.findViewById(R.id.WeightUnitSpinner);
        WeightEditText = (EditText) rootView.findViewById(R.id.WeightEditText);
        WeeksNumberPicker = (NumberPicker) rootView.findViewById(R.id.WeeksNumberPicker);
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

        // Setup the target days
        WeeksNumberPicker.setMinValue(1);
        WeeksNumberPicker.setMaxValue(60);
        WeeksNumberPicker.setValue(prefs.getInt("targetWeeks", 1));

        // Target days listener
        WeeksNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Calculate();
            }
        });

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

        // Initial calculate
        Calculate();

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
        int rawWeight, targetWeightKG;
        try
        {
            rawWeight = Integer.parseInt(WeightEditText.getText().toString());
            if (currentweightunit.equals("Pounds"))
            {
                // Convert from pounds to kg
                targetWeightKG = poundsToKilograms(rawWeight);
            }
            else
                targetWeightKG = rawWeight;
        }
        catch (Exception ex) { return; }

        // Set new user prefs
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("targetWeight", String.format("%d", rawWeight));
        editor.putInt("targetWeeks", WeeksNumberPicker.getValue());
        editor.commit();

        // Calculate diff weight
        int diff = BMIFragment.TheWeightKG - targetWeightKG;
        if (diff < 0)
        {
            // They want to gain weight
        }

        // Calculate calory intake
        int calorieIntake = MealsFragment.CaloriesIn - ExerciseFragment.CaloriesOut;
        if (calorieIntake > 0)
            GainLoseTextView.setText(String.format("Based on your exercise and meals, you will gain %d calories per day.", calorieIntake));
        else if (calorieIntake < 0)
            GainLoseTextView.setText(String.format("Based on your exercise and meals, you will lose %d calories per day.", -calorieIntake));
        else
            GainLoseTextView.setText("Based on your exercise and meals, you will neither gain or lose calories.");

        // Translate to KG
        float kgIntake = calorieIntake / 7716.17917647f;



        // Test for direction
        if (diff < 0 && kgIntake < 0.0f)
        {
            // They want to gain weight but they have negative intake
            TargetTextView.setText("You are trying to gain weight but you are losing weight.");
        }
        else if (diff > 0 && kgIntake > 0.0f)
        {
            // They want to lose weight but they have positive intake
            TargetTextView.setText("You are trying to lose weight but you are gaining weight.");
        }
        else if (kgIntake == 0.0f)
        {
            // They aren't gaining or losing
            TargetTextView.setText("You are neither losing or gaining weight.");
        }
        else
        {
            // Work out weeks
            int days = (int)(-diff / kgIntake);
            TargetTextView.setText(String.format("This means, at your current rate, it will take %d days to reach your target.", days));
        }

        // Work out their ideal daily kg intake
        float idealKgIntake = -diff / ((float)WeeksNumberPicker.getValue() * 7.0f);
        float kgIntakeIncrease = idealKgIntake - kgIntake;

        // Convert back to calories
        int calorieIntakeIncrease = (int)(kgIntakeIncrease * 7716.17917647f);
        if (calorieIntakeIncrease > 0 && diff < 0)
            AdviceTextView.setText(String.format("You should aim to gain %d more calories a day to meet your target.", calorieIntakeIncrease));
        else if (calorieIntakeIncrease < 0 && diff > 0)
            AdviceTextView.setText(String.format("You should aim to lose %d more calories a day to meet your target.", -calorieIntakeIncrease));
        else
            AdviceTextView.setText("You are on track for your target.");

    }
}
