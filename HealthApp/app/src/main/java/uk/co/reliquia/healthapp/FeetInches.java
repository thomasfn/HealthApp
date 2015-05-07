package uk.co.reliquia.healthapp;

/**
 * Represents a height consisting of feet and inches
 */
public class FeetInches
{
    public final int Feet, Inches;

    public FeetInches(int feet, int inches)
    {
        Feet = feet;
        Inches = inches;
    }

    public int toCentimeters()
    {
        return (int)((Feet + Inches / 12.0f) * 30.48f);
    }

    public static FeetInches fromCentimeters(int cm)
    {
        float feet = cm / 30.48f;
        return new FeetInches((int)feet, (int)((feet - (int)feet) * 12.0f));
    }
}
