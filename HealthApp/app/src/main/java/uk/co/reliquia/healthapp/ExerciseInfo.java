package uk.co.reliquia.healthapp;

/*
 * ExerciseInfo - Represents information about a single exercise
 */
public class ExerciseInfo
{
    /*
     * Duration - The duration of this exercise in minutes
     */
    public final int Duration;

    /*
     * Calories - The calories burned per minute by this exercise type
     */
    public final int Calories;

    /*
     * Name - The name of this exercise type
     */
    public final String Name;

    /*
     * ExerciseInfo - The constructor
     */
    public ExerciseInfo(int duration, int calories, String name)
    {
        // Initialise
        Duration = duration;
        Calories = calories;
        Name = name;
    }

    @Override
    public String toString()
    {
        return String.format("%s - %d mins", Name, Duration);
    }

}
