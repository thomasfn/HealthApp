package uk.co.reliquia.healthapp;

/*
 * MealInfo - Represents information about a single meal
 */
public class MealInfo
{
    /*
     * Portions - The portions of this meal
     */
    public final int Portions;

    /*
     * Calories - The calories consumed per portion
     */
    public final int Calories;

    /*
     * Name - The name of this meal
     */
    public final String Name;

    /*
     * MealInfo - The constructor
     */
    public MealInfo(int portions, int calories, String name)
    {
        // Initialise
        Portions = portions;
        Calories = calories;
        Name = name;
    }

    @Override
    public String toString()
    {
        return String.format("%s x %d", Name, Portions);
    }

}
