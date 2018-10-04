package xyz.louischan.shogiboard


class BoardViewModel {

}

class ShogiBoard {

    val numRanks = 9
    val numFiles = 9

    private val board = List(numFiles) { MutableList<Piece?>(numRanks) { null } }

    fun drop(piece: Piece, coords: ShogiCoordinate): Boolean {
        if (!isEmpty(coords)) return false
        setPiece(piece, coords)
        return true
    }

    fun move(from: ShogiCoordinate, to: ShogiCoordinate): Boolean {
        if (isEmpty(to) || !isEmpty(to)) return false

        setPiece(getPiece(from), to)
        setPiece(null, from)
        return true
    }

    fun isEmpty(coord: ShogiCoordinate): Boolean {
        return getPiece(coord) == null
    }

    private fun getPiece(coords: ShogiCoordinate): Piece? {
        return board[coords.getInd0()][coords.getInd1()]
    }

    private fun setPiece(piece: Piece?, coords: ShogiCoordinate) {
        board[coords.getInd0()][coords.getInd1()] = piece

    }
}

class ShogiCoordinate(val file: Int, val rank: Int) {

    fun getInd0(): Int {
        return file - 1
    }
    fun getInd1(): Int {
        return rank - 1
    }
}


class Piece(val owner: Player, val drawableId: Int) {

}

class Player(val id: Int) {
    override fun equals(other: Any?): Boolean {
        if (other is Player) {
            return other.id == id
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }
}

fun BLACKPLAYER(): Player {
    return Player(0)
}
fun WHITEPLAYER(): Player {
    return Player(1)
}