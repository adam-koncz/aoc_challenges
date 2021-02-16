package day15.field

import day15.warrior.Warrior

sealed class Field {

    abstract fun representation(): Char

    override fun toString(): String {
        return this.representation().toString()
    }

}

interface Steppable {
    fun isOccupied(): Boolean

    var warriorStandingOnIt: Warrior?

    var steppableNeighbors: List<Steppable>
}

class Wall: Field() {
    override fun representation() = '#'

}

class OpenCavern(warrior: Warrior? = null) : Field(), Steppable {
    override lateinit var steppableNeighbors: List<Steppable>

    override var warriorStandingOnIt: Warrior? = warrior

    override fun representation(): Char {
        warriorStandingOnIt?.apply { return this.representation }

        return '.'
    }

    override fun isOccupied(): Boolean {
        return warriorStandingOnIt != null
    }

}

