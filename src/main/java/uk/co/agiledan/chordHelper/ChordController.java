package uk.co.agiledan.chordHelper;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChordController
{
	private static final String[] NOTES = {"a", "b", "c", "d", "e", "f", "g"};

	// it's important to identify the letter and then separately the semitone/midi interval
	// even with triple sharps etc, the letters of a major chord will always be 2 apart
	// Each interval needs to contain both the letter interval and the semitone interval (integer notation)
	private static final int[] MAJOR_INTERVALS = {0, 2, 4};
	private static final int[] MINOR_INTERVALS = {0, 2, 4};
	private static final int[] MAJOR_MINOR_SEVENTH_INTERVALS = {0, 2, 4, 6};

	@GetMapping("/")
	public ResponseEntity<ApiResponse> greeting(
		@RequestParam(defaultValue = "c") String root,
		@RequestParam(defaultValue = "major") String chord
	)
	{
		int rootIndex = -1;

		for (int i = 0; i < NOTES.length; i++)
		{
			if (NOTES[i].equals(root))
			{
				rootIndex = i;
				break;
			}
		}

		if (rootIndex == -1)
		{
			return ResponseEntity
				.unprocessableEntity()
				.body(new Error("invalid root note given"));
		}

		int[] chordPattern;

		try
		{
			chordPattern = switch (chord) {
				case "major" -> MAJOR_INTERVALS;
				case "minor" -> MINOR_INTERVALS;
				case "major minor seventh", "dominant seventh" -> MAJOR_MINOR_SEVENTH_INTERVALS;
				default -> throw new Exception("chord was not recognised");
			};
		}
		catch (Exception e)
		{
			return ResponseEntity
				.unprocessableEntity()
				.body(new Error(e.getMessage()));
		}

		ArrayList<String> chordNotes = new ArrayList<>();

		for (int interval : chordPattern)
		{
			int index = rootIndex + interval;
			chordNotes.add(NOTES[index % NOTES.length]);
		}

		return ResponseEntity
			.ok()
			.body(new ChordNotes(String.join(", ", chordNotes)));
	}
}
