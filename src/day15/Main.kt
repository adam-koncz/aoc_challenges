package day15

import day15.field.Field
import day15.field.OpenCavern
import day15.field.Steppable
import day15.field.Wall
import day15.warrior.Elf
import day15.warrior.Goblin
import day15.warrior.Warrior
import java.io.File

const val EXAMPLE_FILE = "resources/day15/example1.txt"
const val EXAMPLE_FILE_2 = "resources/day15/example2.txt"
const val EXAMPLE_FILE_3 = "resources/day15/example3.txt"
const val EXAMPLE_FILE_4 = "resources/day15/example4.txt" // 36334
const val EXAMPLE_FILE_5 = "resources/day15/example5.txt"
const val EXAMPLE_FILE_6 = "resources/day15/example6.txt"
const val EXAMPLE_FILE_7 = "resources/day15/example7.txt"
const val EXAMPLE_FILE_8 = "resources/day15/example8.txt"
const val EXAMPLE_FILE_9 = "resources/day15/example9.txt" // 28944
const val EXAMPLE_FILE_10 = "resources/day15/example10.txt" // 18740
const val CHALLENGE_FILE_1 = "resources/day15/challenge1.txt"

fun main() {
    val lines = File(EXAMPLE_FILE_9).readLines()

    val (steppableFields, elves, goblins ) = parseMap(lines)

    var rounds = 0
    loop@ while (!elves.isEmpty() && !goblins.isEmpty()) {
        val warriorsThatAlreadySteppedThisRound = mutableSetOf<Warrior>()

        val iterator = steppableFields.iterator()
        while (iterator.hasNext()) {
            val warriorStandingOnIt = iterator.next().warriorStandingOnIt
            if (warriorStandingOnIt != null
                && warriorStandingOnIt !in warriorsThatAlreadySteppedThisRound) {
                warriorStandingOnIt.moveAndAttack()
                warriorsThatAlreadySteppedThisRound.add(warriorStandingOnIt)

                elves.removeDead()
                goblins.removeDead()
                if (iterator.hasNext() && (elves.isEmpty() || goblins.isEmpty())) {
                    break@loop
                }
            }
        }
        rounds++


    }
    val result = (elves union goblins).map { it.hitPoints }.sum() * rounds
    println(result)
    println("rounds: " + rounds)


}

fun MutableCollection<out Warrior>.removeDead() {
    val deadOnes = this.filter { it.hitPoints <= 0 }
    this.removeAll(deadOnes)
}

fun parseMap(lines: List<String>): Triple<List<Steppable>, MutableList<Elf>, MutableList<Goblin>> {
    val rows: MutableList<List<Field>> = mutableListOf()
    val elves = mutableListOf<Elf>()
    val goblins = mutableListOf<Goblin>()

    lines.forEach { line ->
        val newRow = mutableListOf<Field>()
        line.forEach { char ->
            when (char) {
                '#' -> newRow.add(Wall())
                '.' -> newRow.add(OpenCavern())
                'E' -> {
                    val elf = Elf()
                    val cavern = OpenCavern(elf)
                    elf.containingField = cavern
                    elves.add(elf)
                    newRow.add(cavern)
                }
                'G' -> {
                    val goblin = Goblin()
                    val cavern = OpenCavern(goblin)
                    goblin.containingField = cavern
                    goblins.add(goblin)
                    newRow.add(cavern)
                }
            }
        }
        rows.add(newRow)
    }
    setUpSteppableNeighbours(rows)
    val steppableFields = rows.flatMap { it }.filter { it is Steppable }.map { it as Steppable }
    return Triple(steppableFields, elves, goblins)
}

fun setUpSteppableNeighbours(map: List<List<Field>>) {
    map.forEachIndexed { idx_y, line ->
        line.forEachIndexed { idx_x, field ->
            val neighbors = mutableListOf<Steppable>()
            if (idx_y > 0 && map[idx_y - 1][idx_x] is Steppable) {
                neighbors.add(map[idx_y - 1][idx_x] as Steppable)
            }
            if (idx_x > 0 && map[idx_y][idx_x - 1] is Steppable) {
                neighbors.add(map[idx_y][idx_x - 1] as Steppable)
            }
            if (idx_x < line.size - 1 && map[idx_y][idx_x + 1] is Steppable) {
                neighbors.add(map[idx_y][idx_x + 1] as Steppable)
            }
            if (idx_y < map.size - 1 && map[idx_y + 1][idx_x] is Steppable) {
                neighbors.add(map[idx_y + 1][idx_x] as Steppable)
            }
            if (field is Steppable) {
                field.steppableNeighbors = neighbors
            }
        }
    }
}





