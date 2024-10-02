package uk.co.agiledan.chordHelper.Factories;

import uk.co.agiledan.chordHelper.DTOs.AccidentalInterface;
import uk.co.agiledan.chordHelper.DTOs.AccidentalInterfaces.Accidental;

public class AccidentalInterfaceFactory
{
    public AccidentalInterface build(String accidental)
    {
        if (accidental.equals("#") || accidental.equals("♯") || accidental.equals("s"))
        {
            return Accidental.sharp();
        }

        if (accidental.equals("b") || accidental.equals("♭"))
        {
            return Accidental.flat();
        }

        return null;
    }
}
