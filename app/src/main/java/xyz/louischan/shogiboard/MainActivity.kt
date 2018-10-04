package xyz.louischan.shogiboard

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import android.widget.RelativeLayout

class MainActivity : AppCompatActivity() {

    lateinit var testPiece: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<RelativeLayout>(R.id.main_layout)

        testPiece = createPieceAt(ShogiCoordinate(9, 1))
        layout.addView(testPiece)
        layout.addView(createPieceAt(ShogiCoordinate(1, 1)))
        layout.addView(createPieceAt(ShogiCoordinate(9, 9)))
        layout.addView(createPieceAt(ShogiCoordinate(1, 9)))
    }

    fun createPieceAt(coords: ShogiCoordinate): RelativeLayout {
        return pieceTo(this.layoutInflater.inflate(R.layout.piece, null) as RelativeLayout, coords)
    }

    fun pieceTo(piece: RelativeLayout, coords: ShogiCoordinate): RelativeLayout {
        val dimens = CoordsAsDimens(coords)

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
        piece.layoutParams = layoutParams
        return piece
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

    inner class CoordsAsDimens(coords: ShogiCoordinate) {
        val file = coords.getIndFile()
        val rank = coords.getIndRank()
        val leftMargin = resources.getDimension(R.dimen.piece_topleft_leftMargin).toInt() +
                file * resources.getDimension(R.dimen.piece_file_separation).toInt()
        val topMargin = resources.getDimension(R.dimen.piece_topleft_topMargin).toInt() +
                rank * resources.getDimension(R.dimen.piece_rank_separation).toInt()
    }
}
