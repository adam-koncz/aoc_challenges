package day7

import java.io.File
import java.io.FileInputStream
import java.util.*

private const val SHINY_GOLD = "shiny gold"

fun main() {
    val scanner = Scanner(FileInputStream(File("resources/day7/challenge1.txt")))
    val bags = mutableListOf<Bag>()

    while (scanner.hasNext()) {

        val bagNames = getBagNamesFromLine(scanner.nextLine())
        val bagToAddContainableBags = bags.createAndAddOrFindBagInCollectionByName(bagNames[0])

        val containableBags = bagNames
            .drop(1)
            .map { bags.createAndAddOrFindBagInCollectionByName(it) }

        bagToAddContainableBags.containableBags = containableBags

    }

    val validOuterBagsForCarryingShinyGoldBags = getValidOuterBagsForCarryingShinyGoldBags(bags)
//    bags.forEach { println(it) }
//    println(bags.size)

    println(validOuterBagsForCarryingShinyGoldBags.size)
}


data class Bag (
    val name: String,

    var containableBags: List<Bag> = listOf()
) {
    override fun toString(): String {
        return "Bag(name=$name, containableBags=${containableBags.map { it.name }})"
    }
}

fun MutableCollection<Bag>.createAndAddOrFindBagInCollectionByName(name: String): Bag {
    val bagInTheCollection = this.find { it.name == name }
    if (bagInTheCollection == null) {
        val newBag = Bag(name)
        this.add(newBag)
        return newBag
    }
    return bagInTheCollection
}

fun getBagNamesFromLine(line: String): List<String> {
    val result = mutableListOf<String>()
    val words = line.split(" ")
    val firstBagName = "${words[0]} ${words[1]}"
    result.add(firstBagName)
    if (line.contains("no")) {
        return result
    }

    val firstContainableBagName = "${words[5]} ${words[6]}"
    result.add(firstContainableBagName)

    val numberOfContainableBags = line.count { it == ',' } + 1
    for (i in 0..numberOfContainableBags - 2) {
        val containableBagName = "${words[9 + i * 4]} ${words[10 + i * 4]}"
        result.add(containableBagName)
    }
    return result
}
fun getValidOuterBagsForCarryingShinyGoldBags(bags: MutableList<Bag>): Set<Bag> {
    val result = mutableSetOf<Bag>()

    bags
        .filter { it.name != SHINY_GOLD }
        .forEach {
            val reachableBags = mutableSetOf<Bag>()
            reachableBags.add(it)
            addAllReachableBagsToResult(reachableBags, it.containableBags)
            if (reachableBags.any { it.name == "shiny gold" }) {
                result.add(it)
                return@forEach
            }
        }

    return result
}

fun addAllReachableBagsToResult(result: MutableSet<Bag>, containableBags: List<Bag>) {
    containableBags.forEach {
        if (!result.contains(it)) {
            result.add(it)
            addAllReachableBagsToResult(result, it.containableBags)
        }
    }
}
