package xyz.louischan.shogiboard

import android.animation.ObjectAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import xyz.louischan.shogiboard.R.layout.piece

class MainActivity : AppCompatActivity() {

    lateinit var model: BoardViewModel
    val pieceMap = hashMapOf<String, PieceView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<RelativeLayout>(R.id.main_layout)

        model = ViewModelProviders.of(this).get(BoardViewModel::class.java)
        renderPieces(model.board.allPieces(), layout)

        model.observeMoves { piece: Piece ->
            movePiece(piece)
        }
    }

    private fun renderPieces(pieces: Iterable<Piece>, layout: RelativeLayout) {
        for (piece in pieces) {
            layout.addView(PieceView(piece).view)
        }
    }

    private fun movePiece(piece: Piece) {
        val pieceView = pieceMap[piece.identifier()]
        if (pieceView != null) {
            movePieceTo(pieceView.view, piece.coords)
        }
    }

    fun movePieceTo(piece: RelativeLayout, coords: ShogiCoordinate) {
        val dimens = CoordsAsDimens(coords)

        if (dimens.file > 0) {
            ObjectAnimator.ofFloat(piece, "x", dimens.leftMargin.toFloat()).apply {
                duration = 2000
                start()
            }
        }
        if (dimens.rank > 0) {
            ObjectAnimator.ofFloat(piece, "y", dimens.topMargin.toFloat()).apply {
                duration = 2000
                start()
            }
        }
    }

    inner class PieceView(piece: Piece) {

        val view: RelativeLayout = layoutInflater.inflate(R.layout.piece, null) as RelativeLayout
        val identifier: String = piece.identifier()

        init {
            view.findViewById<TextView>(R.id.piece_text).text = getString(piece.type)

            val dimens = CoordsAsDimens(piece.coords)

            val layoutParams =
                    FrameLayout.LayoutParams(
                            resources.getDimension(R.dimen.piece_width).toInt(),
                            resources.getDimension(R.dimen.piece_height).toInt()).also {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            it.marginStart = dimens.leftMargin
                        }
                        it.leftMargin = dimens.leftMargin
                        it.topMargin = dimens.topMargin
                    }
            view.layoutParams = layoutParams

            if (piece.owner == WHITEPLAYER()) {
                view.rotation = 180.0f
            }

            pieceMap[identifier] = this
        }
    }

    inner class CoordsAsDimens(coords: ShogiCoordinate) {
        val file = coords.getIndFile()
        val rank = coords.getIndRank()
        val leftMargin = resources.getDimension(R.dimen.piece_topleft_leftMargin).toInt() +
                file * resources.getDimension(R.dimen.piece_file_separation).toInt()
        val topMargin = resources.getDimension(R.dimen.piece_topleft_topMargin).toInt() +
                rank * resources.getDimension(R.dimen.piece_rank_separation).toInt()
    }
}
