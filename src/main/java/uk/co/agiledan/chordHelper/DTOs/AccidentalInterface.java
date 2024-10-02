package uk.co.agiledan.chordHelper.DTOs;

public interface AccidentalInterface
{
    // Bear in mind that a natural symbol is an accidental but doesn't
    // consistently modify the semitone, it depends on the note and the key.
    public int semitoneModifier();
}
