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

import java.util.ArrayList;

/*
 * MealsFragment - The meals calculator fragment
 */
public class MealsFragment extends Fragment
{
    // The argument name for the section number
    private static final String ARG_SECTION_NUMBER = "section_number";

    // The title of the page that this fragment will sit in
    public static final String PAGE_TITLE = "Meals";

    private Spinner MealSpinner;
    private NumberPicker PortionsNumberPicker;
    private TextView CaloriesTextView;
    private Button AddButton, ClearButton;
    private ListView CaloriesListView;

    private ArrayAdapter<CharSequence> mealNamesAdapter;
    private ArrayAdapter<Integer> mealCaloriesAdapter;
    private ArrayAdapter<MealInfo> mealsAdapter;

    private SharedPreferences prefs;

    private ArrayList<MealInfo> mealList;

    /*
     * createInstance - Creates a new instance of this fragment for the given section number
     */
    public static MealsFragment createInstance(int sectionNumber)
    {
        // Initialise it
        MealsFragment fragment = new MealsFragment();

        // Setup the args
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        // Return it
        return fragment;
    }

    /*
     * MealsFragment - Default constructor
     */
    public MealsFragment()
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
        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);

        // Get shared prefs
        prefs = AppContext.getSharedPreferences();

        // Get widgets we need to access
        MealSpinner = (Spinner)rootView.findViewById(R.id.MealSpinner);
        PortionsNumberPicker = (NumberPicker)rootView.findViewById(R.id.PortionsNumberPicker);
        CaloriesTextView = (TextView)rootView.findViewById(R.id.CaloriesTextView);
        AddButton = (Button)rootView.findViewById(R.id.AddButton);
        ClearButton = (Button)rootView.findViewById(R.id.ClearButton);
        CaloriesListView = (ListView)rootView.findViewById(R.id.CaloriesListView);

        // Get adapters and setup the spinners
        mealNamesAdapter = ArrayAdapter.createFromResource(AppContext.getContext(), R.array.meal_names, android.R.layout.simple_spinner_dropdown_item);
        MealSpinner.setAdapter(mealNamesAdapter);
        MealSpinner.setSelection(prefs.getInt("mealSelection", 0));
        PortionsNumberPicker.setMinValue(1);
        PortionsNumberPicker.setMaxValue(5);
        PortionsNumberPicker.setValue(prefs.getInt("portionsSelection", 1));

        // Initialise the list and load from prefs
        mealList = new ArrayList<MealInfo>();
        int mealCount = prefs.getInt("mealCount", 0);
        for (int i = 0; i < mealCount; i++)
        {
            mealList.add(new MealInfo(
                    prefs.getInt(String.format("meal%dPortions", i), 1),
                    prefs.getInt(String.format("meal%dCalories", i), 100),
                    prefs.getString(String.format("meal%dName", i), "Missing")
            ));
        }
        mealsAdapter = new ArrayAdapter<MealInfo>(AppContext.getContext(), android.R.layout.simple_list_item_1, mealList);
        CaloriesListView.setAdapter(mealsAdapter);

        // Hook the buttons
        AddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get the selected items
                int selMeal = MealSpinner.getSelectedItemPosition();
                int selPortions = PortionsNumberPicker.getValue();

                // Lookup the calories
                int calories = getResources().getIntArray(R.array.meal_calories)[selMeal];

                // Insert the entry
                mealList.add(new MealInfo(
                        selPortions,
                        calories,
                        (String) mealNamesAdapter.getItem(selMeal)
                ));

                // Recalculate and save
                calculate();
                save();
                mealsAdapter.notifyDataSetChanged();
            }
        });
        ClearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Clear all items
                mealList.clear();

                // Recalculate and save
                calculate();
                save();
                mealsAdapter.notifyDataSetChanged();
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

        // Loop each meal
        for (int i = 0; i < mealList.size(); i++)
        {
            MealInfo info = mealList.get(i);
            totalCalories += info.Calories * info.Portions;
        }

        // Update UI
        if (totalCalories == 0)
            CaloriesTextView.setText("");
        else
            CaloriesTextView.setText(String.format("You have consumed %d calories!", totalCalories));
    }

    private void save()
    {
        // Get editable prefs
        SharedPreferences.Editor editor = prefs.edit();

        // Write count
        editor.putInt("mealCount", mealList.size());

        // Loop each exercise
        for (int i = 0; i < mealList.size(); i++)
        {
            MealInfo info = mealList.get(i);

            // Write it
            editor.putInt(String.format("meal%dDuration", i), info.Portions);
            editor.putInt(String.format("meal%dCalories", i), info.Calories);
            editor.putString(String.format("meal%dName", i), info.Name);
        }


        // Commit changes
        editor.apply();
    }

}
