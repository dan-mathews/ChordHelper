package uk.co.agiledan.chordHelper.DTOs.AccidentalInterfaces;

import uk.co.agiledan.chordHelper.DTOs.AccidentalInterface;

public record Accidental(int semitoneModifier) implements AccidentalInterface
{
    public static Accidental sharp()
    {
        return new Accidental(1);
    }

    public static Accidental flat()
    {
        return new Accidental(-1);
    }
}
