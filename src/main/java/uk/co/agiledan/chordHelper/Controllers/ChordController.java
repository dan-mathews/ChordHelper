package uk.co.agiledan.chordHelper.Controllers;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.agiledan.chordHelper.ApiResponses.ApiResponse;
import uk.co.agiledan.chordHelper.ApiResponses.ChordNotes;
import uk.co.agiledan.chordHelper.ApiResponses.Error;
import uk.co.agiledan.chordHelper.DTOs.ChordInterval;
import uk.co.agiledan.chordHelper.DTOs.NoteLetter;

@RestController
public class ChordController
{
	private static final NoteLetter NOTE_A = new NoteLetter("A", 0, 0);
	private static final NoteLetter NOTE_B = new NoteLetter("B", 1, 2);
	private static final NoteLetter NOTE_C = new NoteLetter("C", 2, 3);
	private static final NoteLetter NOTE_D = new NoteLetter("D", 3, 5);
	private static final NoteLetter NOTE_E = new NoteLetter("E", 4, 7);
	private static final NoteLetter NOTE_F = new NoteLetter("F", 5, 8);
	private static final NoteLetter NOTE_G = new NoteLetter("G", 6, 10);

	// Map for lookup by note letter
	private static final Map<String, NoteLetter> NOTE_LETTERS_BY_LETTER = Map.of(
		"A", NOTE_A,
		"B", NOTE_B,
		"C", NOTE_C,
		"D", NOTE_D,
		"E", NOTE_E,
		"F", NOTE_F,
		"G", NOTE_G
	);

	// Map for lookup by index (using the same NoteLetter constants)
	private static final Map<Integer, NoteLetter> NOTE_LETTERS_BY_INDEX = Map.of(
		NOTE_A.index(), NOTE_A,
		NOTE_B.index(), NOTE_B,
		NOTE_C.index(), NOTE_C,
		NOTE_D.index(), NOTE_D,
		NOTE_E.index(), NOTE_E,
		NOTE_F.index(), NOTE_F,
		NOTE_G.index(), NOTE_G
	);

	// It's important to identify the letter and then separately the semitone/midi interval.
	// Even with triple sharps etc, the letters of a major chord will always be 2 apart.
	// Each interval needs to contain both the letter interval and the semitone interval (integer notation)
	private static final ChordInterval[] MAJOR_INTERVALS = {
		new ChordInterval(0, 0),
		new ChordInterval(2, 4),
		new ChordInterval(4, 7)
	};

	private static final ChordInterval[] MINOR_INTERVALS = {
		new ChordInterval(0, 0),
		new ChordInterval(2, 3),
		new ChordInterval(4, 7)
	};

	private static final ChordInterval[] MAJOR_MINOR_SEVENTH_INTERVALS = {
		new ChordInterval(0, 0),
		new ChordInterval(2, 4),
		new ChordInterval(4, 7),
		new ChordInterval(6, 10)
	};

	private static final Map<String, ChordInterval[]> CHORD_PATTERN_BY_LABEL = Map.of(
		"major", MAJOR_INTERVALS,
		"minor", MINOR_INTERVALS,
		"major minor seventh", MAJOR_MINOR_SEVENTH_INTERVALS,
		"dominant seventh", MAJOR_MINOR_SEVENTH_INTERVALS
	);

	@GetMapping("/")
	public ResponseEntity<ApiResponse> greeting(
		@RequestParam(defaultValue = "c") String root,
		@RequestParam(defaultValue = "major") String chord
	)
	{
		NoteLetter rootNoteLetter = NOTE_LETTERS_BY_LETTER.get(root.toUpperCase());

		if (rootNoteLetter == null)
		{
			return ResponseEntity
				.unprocessableEntity()
				.body(new Error("invalid root note given"));
		}

		ChordInterval[] chordPattern = CHORD_PATTERN_BY_LABEL.get(chord);

		if (chordPattern == null)
		{
			return ResponseEntity
				.unprocessableEntity()
				.body(new Error("chord was not recognised"));
		}

		ArrayList<String> chordNotes = new ArrayList<>();

		for (ChordInterval interval : chordPattern)
		{
			// First, calculate the letter to add to the chord notes.
			int index = rootNoteLetter.index() + interval.letterInterval();
			int boundedIndex = index % NOTE_LETTERS_BY_INDEX.size();
			NoteLetter noteLetterToAdd = NOTE_LETTERS_BY_INDEX.get(boundedIndex);
			String letterToAdd = noteLetterToAdd.letter();

			// Then, calculate the accidental adjustment required on the letter we're adding.
			int requiredSemitonePosition = rootNoteLetter.semitonePosition() + interval.semitoneInterval();
			int boundedRequiredSemitonePosition = requiredSemitonePosition % 12;
			// The issue is that A is both 0 and 12, so Ab looks 11 semitones sharp from 0,
			// when really it's 1 semitone flat from 12.
			int semitoneDiff = boundedRequiredSemitonePosition - noteLetterToAdd.semitonePosition();
			if (semitoneDiff > 6)
			{
				semitoneDiff = semitoneDiff - 12;
			}

			String accidentalString = "";
			if (semitoneDiff > 0)
			{
				accidentalString += "♯".repeat(semitoneDiff);
			}
			if (semitoneDiff < 0)
			{
				accidentalString += "♭".repeat(Math.abs(semitoneDiff));
			}

			chordNotes.add(letterToAdd + accidentalString);
		}

		return ResponseEntity
			.ok()
			.body(new ChordNotes(String.join(", ", chordNotes)));
	}
}
