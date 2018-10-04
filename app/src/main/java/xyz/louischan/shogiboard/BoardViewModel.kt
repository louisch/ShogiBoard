package xyz.louischan.shogiboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel


class BoardViewModel : ViewModel() {

    val board: ShogiBoard = makeStandardBoard()
    val allChangeObservers = mutableListOf<Observer<ShogiBoard>>()
    val moveObservers = mutableListOf<(Piece) -> Unit>()

    fun observeAnyChange(observer: Observer<ShogiBoard>) {
        allChangeObservers.add(observer)
    }

    fun observeMoves(observer: (Piece) -> Unit) {
        moveObservers.add(observer)
    }

    fun movePiece(from: ShogiCoordinate, to: ShogiCoordinate) {
        val piece = board.move(from, to)
        if (piece != null) {
            moveObservers.forEach { it(piece) }
        }
    }

    fun changeBoard(func: (ShogiBoard) -> Unit) {
        func(board)
        allChangeObservers.forEach { it.onChanged(board) }
    }
}

fun makeStandardBoard(): ShogiBoard {
    val board = ShogiBoard()
    val white = WHITEPLAYER()
    val black = BLACKPLAYER()

    board.setPiece(Piece.L(), white, ShogiCoordinate(1, 1))
    board.setPiece(Piece.N(), white, ShogiCoordinate(2, 1))
    board.setPiece(Piece.S(), white, ShogiCoordinate(3, 1))
    board.setPiece(Piece.G(), white, ShogiCoordinate(4, 1))
    board.setPiece(Piece.KB(), white, ShogiCoordinate(5, 1))
    board.setPiece(Piece.G(), white, ShogiCoordinate(6, 1))
    board.setPiece(Piece.S(), white, ShogiCoordinate(7, 1))
    board.setPiece(Piece.N(), white, ShogiCoordinate(8, 1))
    board.setPiece(Piece.L(), white, ShogiCoordinate(9, 1))

    board.setPiece(Piece.R(), white, ShogiCoordinate(8, 2))
    board.setPiece(Piece.B(), white, ShogiCoordinate(2, 2))

    for (i in 1.until(10)) {
        board.setPiece(Piece.P(), white, ShogiCoordinate(i, 3))
    }

    for (i in 1.until(10)) {
        board.setPiece(Piece.P(), black, ShogiCoordinate(i, 7))
    }

    board.setPiece(Piece.R(), black, ShogiCoordinate(2, 8))
    board.setPiece(Piece.B(), black, ShogiCoordinate(8, 8))

    board.setPiece(Piece.L(), black, ShogiCoordinate(1, 9))
    board.setPiece(Piece.N(), black, ShogiCoordinate(2, 9))
    board.setPiece(Piece.S(), black, ShogiCoordinate(3, 9))
    board.setPiece(Piece.G(), black, ShogiCoordinate(4, 9))
    board.setPiece(Piece.KW(), black, ShogiCoordinate(5, 9))
    board.setPiece(Piece.G(), black, ShogiCoordinate(6, 9))
    board.setPiece(Piece.S(), black, ShogiCoordinate(7, 9))
    board.setPiece(Piece.N(), black, ShogiCoordinate(8, 9))
    board.setPiece(Piece.L(), black, ShogiCoordinate(9, 9))

    return board
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

    fun move(from: ShogiCoordinate, to: ShogiCoordinate): Piece? {
        if (isEmpty(to) || !isEmpty(to)) return null

        val piece = getPiece(from)
        setPiece(piece, to)
        setPiece(null, from)
        return piece
    }

    fun isEmpty(coord: ShogiCoordinate): Boolean {
        return getPiece(coord) == null
    }

    private fun getPiece(coords: ShogiCoordinate): Piece? {
        return board[coords.getIndFile()][coords.getIndRank()]
    }

    fun setPiece(piece: Piece?, coords: ShogiCoordinate) {
        board[coords.getIndFile()][coords.getIndRank()] = piece
    }

    fun setPiece(type: Int, owner: Player, coords: ShogiCoordinate) {
        setPiece(Piece(type, owner, coords), coords)
    }

    fun allPieces(): Iterable<Piece> {
        return board.flatten().filterNotNull()
    }
}

class ShogiCoordinate(val file: Int, val rank: Int) {

    // Indices are from top-left
    fun getIndFile(): Int {
        return 9 - file
    }
    fun getIndRank(): Int {
        return rank - 1
    }

    override fun hashCode(): Int {
        return file * 10 + rank
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShogiCoordinate

        if (file != other.file) return false
        if (rank != other.rank) return false

        return true
    }
}

class Piece(val type: Int, val owner: Player, val coords: ShogiCoordinate) {

    fun identifier(): String {
        return type.toString() + if (owner == BLACKPLAYER()) ":BLACK" else ":WHITE"
    }

    companion object Type {
        fun P(): Int { return R.string.piece_name_P }
        fun L(): Int { return R.string.piece_name_L }
        fun N(): Int { return R.string.piece_name_N }
        fun S(): Int { return R.string.piece_name_S }
        fun G(): Int { return R.string.piece_name_G }
        fun B(): Int { return R.string.piece_name_B }
        fun R(): Int { return R.string.piece_name_R }
        fun KB(): Int { return R.string.piece_name_KB }
        fun KW(): Int { return R.string.piece_name_KW }
    }
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