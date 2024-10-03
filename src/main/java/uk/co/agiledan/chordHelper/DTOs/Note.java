package uk.co.agiledan.chordHelper.DTOs;

public record Note(NoteLetter noteLetter, int semitoneModifier)
{
    public String toString()
    {
        String accidentalString = "";

        if (this.semitoneModifier > 0)
        {
            accidentalString += "♯".repeat(this.semitoneModifier);
        }
        else if (this.semitoneModifier < 0)
        {
            accidentalString += "♭".repeat(Math.abs(this.semitoneModifier));
        }

        return this.noteLetter.letter() + accidentalString;
    }
}
