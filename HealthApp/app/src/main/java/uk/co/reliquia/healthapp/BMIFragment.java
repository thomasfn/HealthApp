package uk.co.reliquia.healthapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/*
 * BMIFragment - The BMI calculator fragment
 */
public class BMIFragment extends Fragment
{

    // The argument name for the section number
    private static final String ARG_SECTION_NUMBER = "section_number";

    // The title of the page that this fragment will sit in
    public static final String PAGE_TITLE = "BMI Calculator";

    private Spinner WeightUnitSpinner, HeightUnitSpinner;
    private EditText WeightTextbox, HeightTextbox1, HeightTextbox2;
    private EditText BMIEditText;
    private TextView ResultTextView;



    private SharedPreferences prefs;

    private ArrayAdapter<CharSequence> weightunitadapter, heightunitadapter, bmicatadapter;
    private CharSequence currentweightunit, currentheightunit;

    private boolean ignore_changes;

    public static  int TheWeightKG;

    /*
     * createInstance - Creates a new instance of this fragment for the given section number
     */
    public static BMIFragment createInstance(int sectionNumber)
    {
        // Initialise it
        BMIFragment fragment = new BMIFragment();

        // Setup the args
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        // Return it
        return fragment;
    }

    /*
     * BMIFragment - Default constructor
     */
    public BMIFragment()
    {
    }

    /*
     * onCreateView - Called when the view for this fragment is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Create the fragment view
        View rootView = inflater.inflate(R.layout.fragment_bmi, container, false);

        // Get shared prefs
        prefs = AppContext.getSharedPreferences();

        // Get widgets we need to access
        WeightUnitSpinner = (Spinner)rootView.findViewById(R.id.WeightUnitSpinner);
        HeightUnitSpinner = (Spinner)rootView.findViewById(R.id.HeightUnitSpinner);
        WeightTextbox = (EditText)rootView.findViewById(R.id.WeightEditText);
        HeightTextbox1 = (EditText)rootView.findViewById(R.id.HeightEditText1);
        HeightTextbox2 = (EditText)rootView.findViewById(R.id.HeightEditText2);
        BMIEditText = (EditText)rootView.findViewById(R.id.BMIEditText);
        BMIEditText.setEnabled(false);
        ResultTextView = (TextView)rootView.findViewById(R.id.ResultTextView);

        // Set the weight units spinner adapter
        weightunitadapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.weight_units_array, android.R.layout.simple_spinner_dropdown_item);
        WeightUnitSpinner.setAdapter(weightunitadapter);

        // Set the height units spinner adapter
        heightunitadapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.height_units_array, android.R.layout.simple_spinner_dropdown_item);
        HeightUnitSpinner.setAdapter(heightunitadapter);

        // Get the BMI category adapter
        bmicatadapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.bmi_categories, android.R.layout.simple_spinner_dropdown_item);

        // Set weight values from user prefs
        WeightUnitSpinner.setSelection(prefs.getInt("weightUnit", 0));
        currentweightunit = weightunitadapter.getItem(WeightUnitSpinner.getSelectedItemPosition());
        WeightTextbox.setText(prefs.getString("weight", "75"));
        setWeightStatic();

        // Set height values from user prefs
        HeightUnitSpinner.setSelection(prefs.getInt("heightUnit", 0));
        currentheightunit = heightunitadapter.getItem(HeightUnitSpinner.getSelectedItemPosition());
        HeightTextbox1.setText(prefs.getString("height1", "150"));
        HeightTextbox2.setText(prefs.getString("height2", "0"));
        if (currentheightunit.equals("Centimeters"))
            HeightTextbox2.setEnabled(false);

        // Weight unit selection listener
        WeightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (ignore_changes) return;
                CharSequence item = weightunitadapter.getItem(position);
                ignore_changes = true;
                setWeightUnit(item);
                ignore_changes = false;

                // Set new user prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("weightUnit", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                if (ignore_changes) return;

            }
        });

        // Height unit selection listener
        HeightUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (ignore_changes) return;
                CharSequence item = heightunitadapter.getItem(position);
                ignore_changes = true;
                setHeightUnit(item);
                ignore_changes = false;

                // Set new user prefs
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("heightUnit", position);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                if (ignore_changes) return;

            }
        });

        // Weight text box listener
        TextWatcher watcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (ignore_changes) return;
                setWeightStatic();
                Calculate();
            }
        };

        WeightTextbox.addTextChangedListener(watcher);
        HeightTextbox1.addTextChangedListener(watcher);
        HeightTextbox2.addTextChangedListener(watcher);

        // Initial calculate
        Calculate();


        return rootView;
    }

    private void setWeightStatic()
    {
        try
        {
            int rawWeight = Integer.parseInt(WeightTextbox.getText().toString());
            if (currentweightunit.equals("Pounds"))
            {
                // Convert from pounds to kg
                TheWeightKG = poundsToKilograms(rawWeight);
            }
            else
                TheWeightKG = rawWeight;
            ViewPager thing;
            MainActivity.mViewPager.getAdapter().notifyDataSetChanged();
        }
        catch (Exception ex) { }
    }

    private void setWeightUnit(CharSequence weightunit)
    {
        // Check if a change happened
        if (currentweightunit.equals(weightunit)) return;

        // Convert between the units
        if (currentweightunit.equals("Kilograms") && weightunit.equals("Pounds"))
        {
            // Convert from kg to pounds
            int kg = Integer.parseInt(WeightTextbox.getText().toString());
            WeightTextbox.setText(String.format("%d", kilogramsToPounds(kg)));
        }
        if (currentweightunit.equals("Pounds") && weightunit.equals("Kilograms"))
        {
            // Convert from pounds to kg
            int pounds = Integer.parseInt(WeightTextbox.getText().toString());
            WeightTextbox.setText(String.format("%d", poundsToKilograms(pounds)));
        }

        // Store
        currentweightunit = weightunit;
    }

    private void setHeightUnit(CharSequence heightunit)
    {
        // Check if a change happened
        if (currentheightunit.equals(heightunit)) return;

        // Convert between the units
        if (currentheightunit.equals("Feet/Inches") && heightunit.equals("Centimeters"))
        {
            // Convert from feet to cm
            int feet = Integer.parseInt(HeightTextbox1.getText().toString());
            int inches = Integer.parseInt(HeightTextbox2.getText().toString());
            FeetInches fi = new FeetInches(feet, inches);
            HeightTextbox1.setText(String.format("%d", fi.toCentimeters()));
            HeightTextbox2.setEnabled(false);
            HeightTextbox2.setText("0");
        }
        if (currentheightunit.equals("Centimeters") && heightunit.equals("Feet/Inches"))
        {
            // Convert from cm to feet
            int cm = Integer.parseInt(HeightTextbox1.getText().toString());
            FeetInches fi = FeetInches.fromCentimeters(cm);

            HeightTextbox1.setText(String.format("%d", fi.Feet));
            HeightTextbox2.setText(String.format("%d", fi.Inches));
            HeightTextbox2.setEnabled(true);
        }

        // Store
        currentheightunit = heightunit;
    }

    private static int kilogramsToPounds(int kg)
    {
        return (int)(kg * 2.20462f);
    }

    private static int poundsToKilograms(int kg)
    {
        return (int)(kg / 2.20462f);
    }

    private void Calculate()
    {
        // Get input from text boxes as numbers
        int weightraw, heightraw1, heightraw2;
        try
        {
            weightraw = Integer.parseInt(WeightTextbox.getText().toString());
            heightraw1 = Integer.parseInt(HeightTextbox1.getText().toString());
            heightraw2 = Integer.parseInt(HeightTextbox2.getText().toString());
        }
        catch (Exception ex)
        {
            return;
        }

        // Set new user prefs
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("weight", String.format("%d", weightraw));
        editor.putString("height1", String.format("%d", heightraw1));
        editor.putString("height2", String.format("%d", heightraw2));
        editor.commit();

        // Convert to metric units if needed
        int weightkg;
        if (currentweightunit.equals("Kilograms"))
            weightkg = weightraw;
        else
            weightkg = poundsToKilograms(weightraw);

        int heightcm;
        if (currentheightunit.equals("Centimeters"))
            heightcm = heightraw1;
        else
            heightcm = new FeetInches(heightraw1, heightraw2).toCentimeters();

        // Calculate BMI
        float bmi = weightkg / (heightcm * heightcm / 10000.0f);
        BMIEditText.setText(String.format("%f", bmi));

        // Calculate category
        int cat = 0;
        if (bmi < 16.0f)
            cat = 1;
        else if (bmi < 18.5f)
            cat = 2;
        else if (bmi < 25.0f)
            cat = 3;
        else if (bmi < 30.0f)
            cat = 4;
        else if (bmi < 35.0f)
            cat = 5;
        else if (bmi < 40.0f)
            cat = 6;
        else if (bmi < 100.0f)
            cat = 7;
        else
            cat = 8;
        ResultTextView.setText(bmicatadapter.getItem(cat));
    }
}