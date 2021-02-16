package day15.field

/**
 *  Since the warrior can only step 1 field, I don't need to store every step of the route.
 *  The firstStep is necessary, so I
 */
data class Route(
    val firstStep: Steppable,

    val lastStep: Steppable,

)