package uk.co.agiledan.chordHelper.Factories;

import uk.co.agiledan.chordHelper.DTOs.AccidentalInterface;
import uk.co.agiledan.chordHelper.DTOs.NoteLetter;

public class NoteLetterFactory
{
    public NoteLetter build(NoteLetter noteLetter, AccidentalInterface accidental)
    {
        return new NoteLetter(
            noteLetter.letter(),
            noteLetter.index(),
            noteLetter.semitonePosition() + accidental.semitoneModifier()
        );
    }
}
