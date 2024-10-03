package uk.co.agiledan.chordHelper.DTOs;

import java.util.Map;

public record NoteLetter(String letter, int index, int semitonePosition)
{
    public static final NoteLetter NOTE_A = new NoteLetter("A", 0, 0);
	public static final NoteLetter NOTE_B = new NoteLetter("B", 1, 2);
	public static final NoteLetter NOTE_C = new NoteLetter("C", 2, 3);
	public static final NoteLetter NOTE_D = new NoteLetter("D", 3, 5);
	public static final NoteLetter NOTE_E = new NoteLetter("E", 4, 7);
	public static final NoteLetter NOTE_F = new NoteLetter("F", 5, 8);
	public static final NoteLetter NOTE_G = new NoteLetter("G", 6, 10);

	// Map for lookup by letter
	public static final Map<String, NoteLetter> NOTE_LETTERS_BY_LETTER = Map.of(
		"A", NOTE_A,
		"B", NOTE_B,
		"C", NOTE_C,
		"D", NOTE_D,
		"E", NOTE_E,
		"F", NOTE_F,
		"G", NOTE_G
	);

	// Map for lookup by index
	public static final Map<Integer, NoteLetter> NOTE_LETTERS_BY_INDEX = Map.of(
		NOTE_A.index(), NOTE_A,
		NOTE_B.index(), NOTE_B,
		NOTE_C.index(), NOTE_C,
		NOTE_D.index(), NOTE_D,
		NOTE_E.index(), NOTE_E,
		NOTE_F.index(), NOTE_F,
		NOTE_G.index(), NOTE_G
	);
}
