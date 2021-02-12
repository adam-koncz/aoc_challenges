package day16

private val rangeMatcher = """\d+-\d+""".toRegex()

fun readNoteFromLines(lines: List<String>, numberOfNoteRows: Int): Note {
    val noteRows = mutableListOf<NoteRow>()
    for (i in 0 until numberOfNoteRows) {
        val line = lines[i]
        val nameOfRow = line.substringBefore(":")
        val ranges = mutableListOf<IntRange>()
        rangeMatcher.findAll(line)
            .map { it.groupValues[0] }
            .forEach {
                val start = it.substringBefore("-").toInt()
                val end = it.substringAfter("-").toInt()
                ranges.add(IntRange(start, end))
            }
        noteRows.add(NoteRow(nameOfRow, ranges))
    }
    return Note(rows = noteRows)
}

fun readNearbyTicketsFromLines(lines: List<String>, linesToSkip: Int): List<List<Int>> {
    return lines
        .drop(linesToSkip)
        .map {
            it.split(",")
                .map { it.toInt() }
        }
}