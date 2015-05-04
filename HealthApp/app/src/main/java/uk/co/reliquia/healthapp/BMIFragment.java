package uk.co.reliquia.healthapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * BMIFragment - The BMI calculator fragment
 */
public class BMIFragment extends Fragment
{

    // The argument name for the section number
    private static final String ARG_SECTION_NUMBER = "section_number";

    // The title of the page that this fragment will sit in
    public static final String PAGE_TITLE = "BMI Calculator";

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
                             Bundle savedInstanceState) {
        // Create the fragment view
        View rootView = inflater.inflate(R.layout.fragment_bmi, container, false);
        return rootView;
    }
}