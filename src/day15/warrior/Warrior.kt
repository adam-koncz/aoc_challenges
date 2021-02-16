package day15.warrior

import day15.field.Route
import day15.field.Steppable

sealed class Warrior {

    abstract val representation: Char

    var hitPoints: Int = 200

    private val attackPower: Int = 3

    lateinit var containingField: Steppable

    fun die() {
        containingField.warriorStandingOnIt = null
    }

    fun attack(other: Warrior) {
        other.hitPoints -= this.attackPower
        if (other.hitPoints <= 0) {
            other.die()
        }
    }

    /*
    There are 3 cases:
     - The enemy is right next to the Warrior already. It only needs to attack. If there are more, it attacks the one
       with the lowest HP.
     - The enemy is within reach to the Warrior. This means that the warrior can attack the enemy after it moves 1
       field.
     - The warrior cannot reach any enemy. It searches for the closest enemy, and takes a step towards it.
     */
    fun moveAndAttack() {

        /*
            First case.
         */
        val neighborToAttack = findTargetToAttack(containingField)
        if (neighborToAttack != null) {
            this.attack(neighborToAttack)
            return
        }

        /*
            Second case
         */
        containingField
            .steppableNeighbors
            .filter { !it.isOccupied() }
            .forEach {
                val enemyToAttack = findTargetToAttack(it)
                if (enemyToAttack != null) {
                    moveToSteppable(it)
                    attack(enemyToAttack)
                    return
                }
            }


        /**
         * Third case. This is the tricky one. I use BFS (Breadth-first search) to explore the reachable fields.
         * I am looking for enemies, and I want to find the closest one.
         * Since I am using BFS, I can be sure that the first found
         * enemy will be one of the closest ones (if multiple exists with the same distance)
         */
        var targetRoute: Route? = null
        val visitedFields = mutableListOf<Steppable>()
        val fieldsToVisit = mutableListOf<Route>()

        visitedFields.add(this.containingField)

        containingField
            .steppableNeighbors
            .filter { !it.isOccupied() }
            .forEach { neighbourField ->
                visitedFields.add(neighbourField)
                neighbourField
                    .steppableNeighbors
                    .filter { !it.isOccupied() }
                    .filter { it !in visitedFields }
                    .forEach { neighboursNeighbour ->
                        fieldsToVisit.add(
                            Route(firstStep = neighbourField, lastStep = neighboursNeighbour),
                        )
                    }
            }

        while (!fieldsToVisit.isEmpty() && targetRoute == null) {
            val route = fieldsToVisit.removeAt(0)
            val fieldToVisit = route.lastStep
            val enemyToAttack = findTargetToAttack(fieldToVisit)
            if (enemyToAttack != null) {
                targetRoute = route
            }
            visitedFields.add(fieldToVisit)
            fieldToVisit
                .steppableNeighbors
                .filter { !it.isOccupied() }
                .filter { it !in visitedFields && it !in fieldsToVisit.map { it.lastStep } }
                .forEach {
                    fieldsToVisit.add(route.copy(lastStep = it))
                }
        }


        if (targetRoute == null) {
            return
        }
        this.moveToSteppable(targetRoute.firstStep)

    }

    private fun findTargetToAttack(field: Steppable) = field
        .steppableNeighbors
        .filter { it.isOccupied() }
        .filter { it.warriorStandingOnIt!!.representation != this.representation }
        .sortedBy { it.warriorStandingOnIt?.hitPoints }
        .map { it.warriorStandingOnIt!! }
        .firstOrNull()


    private fun moveToSteppable(newSteppable: Steppable) {
        this.containingField.warriorStandingOnIt = null
        this.containingField = newSteppable
        newSteppable.warriorStandingOnIt = this
    }
}

class Goblin : Warrior() {

    override val representation: Char
        get() = 'G'
}

class Elf : Warrior() {

    override val representation: Char
        get() = 'E'
}
