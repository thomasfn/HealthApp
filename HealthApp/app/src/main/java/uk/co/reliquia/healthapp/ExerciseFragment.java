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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/*
 * ExerciseFragment - The exercise calculator fragment
 */
public class ExerciseFragment extends Fragment
{
    // The argument name for the section number
    private static final String ARG_SECTION_NUMBER = "section_number";

    // The title of the page that this fragment will sit in
    public static final String PAGE_TITLE = "Exercise";

    private Spinner ExerciseSpinner, DurationSpinner;
    private TextView CaloriesTextView;
    private Button AddButton, ClearButton;
    private ListView CaloriesListView;

    private ArrayAdapter<CharSequence> exerciseNamesAdapter, durationNamesAdapter;
    private ArrayAdapter<Integer> exerciseCaloriesAdapter, durationValuesAdapter;
    private ArrayAdapter<ExerciseInfo> exercisesAdapter;

    private SharedPreferences prefs;

    private ArrayList<ExerciseInfo> exerciseList;

    /*
     * createInstance - Creates a new instance of this fragment for the given section number
     */
    public static ExerciseFragment createInstance(int sectionNumber)
    {
        // Initialise it
        ExerciseFragment fragment = new ExerciseFragment();

        // Setup the args
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        // Return it
        return fragment;
    }

    /*
     * ExerciseFragment - Default constructor
     */
    public ExerciseFragment()
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
        View rootView = inflater.inflate(R.layout.fragment_exercise, container, false);

        // Get shared prefs
        prefs = AppContext.getSharedPreferences();

        // Get widgets we need to access
        ExerciseSpinner = (Spinner)rootView.findViewById(R.id.ExerciseSpinner);
        DurationSpinner = (Spinner)rootView.findViewById(R.id.DurationSpinner);
        CaloriesTextView = (TextView)rootView.findViewById(R.id.CaloriesTextView);
        AddButton = (Button)rootView.findViewById(R.id.AddButton);
        ClearButton = (Button)rootView.findViewById(R.id.ClearButton);
        CaloriesListView = (ListView)rootView.findViewById(R.id.CaloriesListView);

        // Get adapters and setup the spinners
        exerciseNamesAdapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.exercise_names, android.R.layout.simple_spinner_dropdown_item);
        ExerciseSpinner.setAdapter(exerciseNamesAdapter);
        ExerciseSpinner.setSelection(prefs.getInt("exerciseSelection", 0));
        durationNamesAdapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.duration_names, android.R.layout.simple_spinner_dropdown_item);
        DurationSpinner.setAdapter(durationNamesAdapter);
        DurationSpinner.setSelection(prefs.getInt("durationSelection", 0));

        // Initialise the list and load from prefs
        exerciseList = new ArrayList<ExerciseInfo>();
        int exerciseCount = prefs.getInt("exerciseCount", 0);
        for (int i = 0; i < exerciseCount; i++)
        {
            exerciseList.add(new ExerciseInfo(
                    prefs.getInt(String.format("exercise%dDuration", i), 5),
                    prefs.getInt(String.format("exercise%dCalories", i), 10),
                    prefs.getString(String.format("exercise%dName", i), "Missing")
            ));
        }
        exercisesAdapter = new ArrayAdapter<ExerciseInfo>(AppContext.getContext(), android.R.layout.simple_list_item_1, exerciseList);
        CaloriesListView.setAdapter(exercisesAdapter);

        // Hook the buttons
        AddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get the selected items
                int selExercise = ExerciseSpinner.getSelectedItemPosition();
                int selDuration = DurationSpinner.getSelectedItemPosition();

                // Lookup the calories and minutes
                int calories = getResources().getIntArray(R.array.exercise_calories)[selExercise];
                int mins = getResources().getIntArray(R.array.duration_values)[selDuration];

                // Insert the entry
                exerciseList.add(new ExerciseInfo(
                        mins,
                        calories,
                        (String) exerciseNamesAdapter.getItem(selExercise)
                ));

                // Recalculate and save
                calculate();
                save();
                exercisesAdapter.notifyDataSetChanged();
            }
        });
        ClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Clear all items
                exerciseList.clear();

                // Recalculate and save
                calculate();
                save();
                exercisesAdapter.notifyDataSetChanged();
            }
        });

        // Initial calculate
        calculate();

        // Return the view
        return rootView;
    }

    private void calculate()
    {
        // Declare total calories
        int totalCalories = 0;

        // Loop each exercise
        for (int i = 0; i < exerciseList.size(); i++)
        {
            ExerciseInfo info = exerciseList.get(i);
            totalCalories += info.Calories * info.Duration;
        }

        // Update UI
        if (totalCalories == 0)
            CaloriesTextView.setText("");
        else
            CaloriesTextView.setText(String.format("You have burned %d calories!", totalCalories));
    }

    private void save()
    {
        // Get editable prefs
        SharedPreferences.Editor editor = prefs.edit();

        // Write count
        editor.putInt("exerciseCount", exerciseList.size());

        // Loop each exercise
        for (int i = 0; i < exerciseList.size(); i++)
        {
            ExerciseInfo info = exerciseList.get(i);

            // Write it
            editor.putInt(String.format("exercise%dDuration", i), info.Duration);
            editor.putInt(String.format("exercise%dCalories", i), info.Calories);
            editor.putString(String.format("exercise%dName", i), info.Name);
        }


        // Commit changes
        editor.apply();
    }

}
