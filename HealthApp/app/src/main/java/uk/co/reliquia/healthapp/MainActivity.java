package uk.co.reliquia.healthapp;

import java.util.Locale;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends FragmentActivity
{

    /*
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /*
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /*
     * The buttons that allow the user to navigate the pager
     */
    private ImageButton mWeightButton, mExerciseButton, mMealsButton;



    /*
     * onCreate - Called when this activity has been created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call base class
        super.onCreate(savedInstanceState);

        // Set content view
        setContentView(R.layout.activity_main);

        // Set app context on static class for other classes to use
        AppContext.setContext(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the buttons
        mWeightButton = (ImageButton) findViewById(R.id.weight_button);
        mExerciseButton = (ImageButton) findViewById(R.id.exercise_button);
        mMealsButton = (ImageButton) findViewById(R.id.meals_button);

        // Handle click events
        mWeightButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.this.mViewPager.setCurrentItem(0, true);
            }
        });
        mExerciseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.this.mViewPager.setCurrentItem(1, true);
            }
        });
        mMealsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.this.mViewPager.setCurrentItem(2, true);
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*
         * getItem - Gets the fragment for the page at the specified position
         */
        @Override
        public Fragment getItem(int position)
        {
            // What page is it?
            switch (position)
            {
                case 0:
                    // Create the BMI fragment
                    return BMIFragment.createInstance(position + 1);
                case 1:
                    // Create the exercise fragment
                    return ExerciseFragment.createInstance(position + 1);
                case 2:
                    // Create the meal fragment
                    return MealsFragment.createInstance(position + 1);
                default:
                    // Create a placeholder fragment
                    return PlaceholderFragment.newInstance(position + 1);
            }

        }

        /*
         * getItem - Gets the number of pages
         */
        @Override
        public int getCount()
        {
            // Show 3 total pages
            return 3;
        }

        /*
         * getPageTitle - Gets the title of the specified page
         */
        @Override
        public CharSequence getPageTitle(int position)
        {
            // Get default locale
            Locale l = Locale.getDefault();

            // What page is it?
            switch (position)
            {
                case 0:
                    return BMIFragment.PAGE_TITLE.toUpperCase(l);
                case 1:
                    return ExerciseFragment.PAGE_TITLE.toUpperCase(l);
                case 2:
                    return MealsFragment.PAGE_TITLE.toUpperCase(l);
            }

            // Invalid position
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
