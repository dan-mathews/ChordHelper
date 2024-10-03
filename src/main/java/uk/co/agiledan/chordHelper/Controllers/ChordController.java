package uk.co.agiledan.chordHelper.Controllers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.agiledan.chordHelper.ApiResponses.ApiResponse;
import uk.co.agiledan.chordHelper.ApiResponses.ChordNotes;
import uk.co.agiledan.chordHelper.ApiResponses.Error;
import uk.co.agiledan.chordHelper.DTOs.AccidentalInterface;
import uk.co.agiledan.chordHelper.DTOs.ChordInterval;
import uk.co.agiledan.chordHelper.DTOs.Note;
import uk.co.agiledan.chordHelper.Factories.AccidentalInterfaceFactory;
import uk.co.agiledan.chordHelper.Factories.NoteLetterFactory;
import uk.co.agiledan.chordHelper.DTOs.NoteLetter;

@RestController
public class ChordController
{
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
		String rootLetter = root.substring(0, 1);
		NoteLetter rootNoteLetter = NoteLetter.NOTE_LETTERS_BY_LETTER.get(rootLetter.toUpperCase());

		if (rootNoteLetter == null)
		{
			Set<String> validNoteLetters = NoteLetter.NOTE_LETTERS_BY_LETTER.keySet();

			return ResponseEntity
				.unprocessableEntity()
				.body(new Error("root note must start with one of: " + String.join(", ", validNoteLetters)));
		}

		if (root.length() > 2)
		{
			return ResponseEntity
				.unprocessableEntity()
				.body(new Error("root note cannot be more than two characters"));
		}

		if (root.length() == 2)
		{
			String accidentalString = root.substring(1, 2);

			AccidentalInterfaceFactory accidentalInterfaceFactory = new AccidentalInterfaceFactory();
			AccidentalInterface accidental = accidentalInterfaceFactory.build(accidentalString);

			if (accidental == null)
			{
				return ResponseEntity
					.unprocessableEntity()
					.body(new Error("root note accidental not recognised"));
			}

			NoteLetterFactory noteLetterFactory = new NoteLetterFactory();
			rootNoteLetter = noteLetterFactory.build(rootNoteLetter, accidental);
		}

		ChordInterval[] chordPattern = CHORD_PATTERN_BY_LABEL.get(chord);

		if (chordPattern == null)
		{
			Set<String> validChords = CHORD_PATTERN_BY_LABEL.keySet();

			return ResponseEntity
				.unprocessableEntity()
				.body(new Error("chord must be one of: " + String.join(", ", validChords)));
		}

		ArrayList<String> chordNotes = new ArrayList<>();

		for (ChordInterval interval : chordPattern)
		{
			//TODO move to NoteLetterFactory
			// First, calculate the letter to add to the chord notes.
			int index = rootNoteLetter.index() + interval.letterInterval();
			int boundedIndex = index % NoteLetter.NOTE_LETTERS_BY_INDEX.size();
			NoteLetter noteLetterToAdd = NoteLetter.NOTE_LETTERS_BY_INDEX.get(boundedIndex);

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

			Note note = new Note(noteLetterToAdd, semitoneDiff);

			chordNotes.add(note.toString());
		}

		return ResponseEntity
			.ok()
			.body(new ChordNotes(String.join(", ", chordNotes)));
	}
}
