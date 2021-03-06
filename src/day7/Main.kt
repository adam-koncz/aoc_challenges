package day7

import java.io.File
import java.io.FileInputStream
import java.util.*

private const val SHINY_GOLD = "shiny gold"
private const val CHALLENGE_INPUT_FILE = "resources/day7/challenge1.txt"

fun main() {
    solveFirstHalfOfTheChallenge()

    solveSecondHalfOfTheChallenge()
}


private fun solveFirstHalfOfTheChallenge() {
    val scanner = Scanner(FileInputStream(File(CHALLENGE_INPUT_FILE)))
    val bags = mutableListOf<Bag>()

    readBagsDataFromScanner(scanner, bags)

    val validOuterBagsForCarryingShinyGoldBags = getValidOuterBagsForCarryingShinyGoldBags(bags)
    println("The solution for the first half of the challenge: " + validOuterBagsForCarryingShinyGoldBags.size)
}

private fun solveSecondHalfOfTheChallenge() {
    val scanner = Scanner(FileInputStream(File(CHALLENGE_INPUT_FILE)))
    val bags = mutableListOf<Bag>()

    readBagsDataFromScanner(scanner, bags)

    val requiredBagsInsideBagForBagName = getTotalNumberOfBagsPresentForRootBag(bags.first { it.name == SHINY_GOLD }) - 1
    println("The solution for the second half of the challenge: " + requiredBagsInsideBagForBagName)
}


data class Bag (
    val name: String,

    val containableBags: MutableMap<Bag, Int> = mutableMapOf()
) {
    override fun toString(): String {
        return "Bag(name=$name, containableBags=${containableBags.map { "${it.key.name}=${it.value}" }})"
    }
}
private fun readBagsDataFromScanner(scanner: Scanner, bags: MutableList<Bag>) {
    while (scanner.hasNext()) {

        val words = scanner.nextLine().split(" ")
        val bagToAddContainableBagsName = "${words[0]} ${words[1]}"
        val bagToAddContainableBags: Bag = bags.createAndAddOrFindBagInCollectionByName(bagToAddContainableBagsName)

        val containableBagsMap = getContainableBagsMapFromWords(words.drop(4), bags)
        bagToAddContainableBags.containableBags.plusAssign(containableBagsMap)

    }
    scanner.close()
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

fun getContainableBagsMapFromWords(words: List<String>, bags: MutableCollection<Bag>): Map<Bag, Int> {
    if (words.any { it == "no" }) {
        return emptyMap()
    }

    val result = mutableMapOf<Bag, Int>()

    val firstContainableBag = bags.createAndAddOrFindBagInCollectionByName("${words[1]} ${words[2]}")
    result.plusAssign(firstContainableBag to words[0].toInt())
    val numberOfContainableBags = words.size / 4

    for (i in 0..numberOfContainableBags - 2) {
        val containableBag = bags.createAndAddOrFindBagInCollectionByName("${words[5 + i * 4]} ${words[6 + i * 4]}")
        result.plusAssign(containableBag to words[4 + i * 4].toInt())
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
            addAllReachableBagsRecursively(reachableBags, it.containableBags.keys)
            if (reachableBags.any { it.name == SHINY_GOLD }) {
                result.add(it)
            }
        }
    return result
}

fun addAllReachableBagsRecursively(result: MutableSet<Bag>, containableBags: Set<Bag>) {
    containableBags.forEach {
        if (!result.contains(it)) {
            result.add(it)
            addAllReachableBagsRecursively(result, it.containableBags.keys)
        }
    }
}
fun getTotalNumberOfBagsPresentForRootBag(rootBag: Bag): Int {
    if (rootBag.containableBags.isEmpty()) {
        return 1
    }
    return 1 + rootBag.containableBags.map { it.value * getTotalNumberOfBagsPresentForRootBag(it.key)}.sum()
}
