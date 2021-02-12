package day16

import java.io.File

const val EXAMPLE_FILE = "resources/day16/example1.txt"
const val CHALLENGE_FILE = "resources/day16/challenge1.txt"

data class Note(
    val rows: List<NoteRow>,
)

data class NoteRow(
    val name: String,

    val ranges: List<IntRange>,

    ) {
    fun isIntInAnyRange(number: Int): Boolean {
        return this.ranges.any { number in it }
    }
}

fun main() {

    // reading in an parsing input
    val lines = File(CHALLENGE_FILE).readLines()
    val numberOfNoteRows = lines.indexOfFirst { it == "" }

    val note = readNoteFromLines(lines, numberOfNoteRows)

    val myTicket = lines[numberOfNoteRows + 2].split(",").map { it.toInt() }

    val nearbyTickets: List<List<Int>> = readNearbyTicketsFromLines(
        lines = lines,
        linesToSkip = numberOfNoteRows + 5
    )

//    println(note)
//    println(myTicket)
//    println(nearbyTickets)

    //--------------------
    // first task

    solveFirstTask(note, nearbyTickets)


    //--------------------
    // second task
    solveSecondTask(note, myTicket, nearbyTickets)

}


private fun solveFirstTask(
    note: Note,
    nearbyTickets: List<List<Int>>
) {
    val allRanges = note.rows.flatMap { it.ranges }
    val sumOfNumbersNotValidForAnyField = nearbyTickets
        .flatMap { it }
        .filter { num -> allRanges.none { it.contains(num) } }
        .sum()

    println("The solution for the first task: " + sumOfNumbersNotValidForAnyField)
}

fun solveSecondTask(note: Note, myTicket: List<Int>, nearbyTickets: List<List<Int>>) {
    val validTickets: List<List<Int>> = filterInvalidTickets(nearbyTickets, note)
    val numOfFeatures = note.rows.size

    /*
        2D Boolean array init
        It is initialized to all trues, because at the beginning we know nothing,
        every number on the ticket can represent every feature of the ticket.
        The columns represent the features, and the rows represent the indices of numbers on the ticket
     */
    val possibleMatches = Array(numOfFeatures, { BooleanArray(numOfFeatures) { true } })


    /*
        if in any ticket, a number isn't in the valid ranges of a feature, we can be sure
        that the numbers in the same column doesn't represent that feature in the ticket
     */
    validTickets.forEach { ticket ->
        ticket.forEachIndexed { idx_y, number ->
                note.rows.forEachIndexed { idx_x, noteRow ->
                    if (!noteRow.isIntInAnyRange(number)) {
                        possibleMatches[idx_y][idx_x] = false
                    }
                }
        }
    }

    /*
        If we have a situation like this: 1 1 0
                                          1 0 0
                                          0 0 1
        We can be sure sure that the second feature is represented by the first number on the ticket, and
        the third feature is represented by the third number on the ticket. Since numbers only represent one
        feature, we can eliminate the 1 in the top left corner, so only one 'one' stays in each of the columns.
     */
    while (!possibleMatches.hasOnlyOneTrueEveryColumn()) {
        for (i in 0 until possibleMatches.size) {
            val column = possibleMatches.getColumnOfValues(i)
            if (column.hasOnlyOneTrue()) {
                val idxOfTrue = column.indexOfFirst { it == true }
                possibleMatches.setEveryValueToFalseInRowExceptOne(idxOfTrue, i)
            }
        }
    }

    /*
        Gathering the indices of the rows that start with "departure"
     */
    val idxOfRowsThatStartWithDeparture = mutableListOf<Int>()
    for (i in 0 until note.rows.size) {
        if (note.rows[i].name.startsWith("departure")) {
            idxOfRowsThatStartWithDeparture.add(i)
        }
    }

    /*
        Gathering the indices of the numbers that represent the rows
     */
    val indicesOfNumbersToBeMultiplied = mutableListOf<Int>()
    for (i in idxOfRowsThatStartWithDeparture) {
        val value = possibleMatches.getColumnOfValues(i).indexOfFirst { it == true }
        indicesOfNumbersToBeMultiplied.add(value)
    }


    val result = indicesOfNumbersToBeMultiplied.fold(1L) {
            acc, idx -> acc * myTicket[idx]
    }

    println("The solution of the second task is: " + result)

//    for debug purposes
//    printTwoDimensionalArray(possibleMatches)

}

private fun filterInvalidTickets(
    nearbyTickets: List<List<Int>>,
    note: Note,
): List<List<Int>> {
    val allRanges = note.rows.flatMap { it.ranges }
    return nearbyTickets
        .filter { ticketNumsList ->
            ticketNumsList.all { num -> allRanges.any { range -> num in range } }
        }

}

// only works for square matrices now
fun Array<BooleanArray>.getColumnOfValues(column: Int): BooleanArray {
    val result = BooleanArray(this.size)
    for (i in 0 until this.size) {
        result[i] = this.get(i).get(column)
    }
    return result
}

// only works for square matrices now
fun Array<BooleanArray>.hasOnlyOneTrueEveryColumn(): Boolean {
    for (i in 0 until this.size) {
        if (!this.getColumnOfValues(i).hasOnlyOneTrue()) {
            return false
        }
    }
    return true

}

fun BooleanArray.hasOnlyOneTrue(): Boolean {
    return this.count { it == true } == 1
}

fun Array<BooleanArray>.setEveryValueToFalseInRowExceptOne(rowIndex: Int, exceptionalIndex: Int) {
    for (i in 0 until this[rowIndex].size) {
        if (i != exceptionalIndex) {
            this[rowIndex][i] = false
        }
    }
}

private fun printTwoDimensionalArray(possibleMatches: Array<BooleanArray>) {
    for (array in possibleMatches) {
        var result = ""
        for (bool in array) {
            result += "${if (bool) 1 else 0} "
        }
        println(result)
    }
}







