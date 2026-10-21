package com.example.chesslab

class ChessEngine {
    fun bestMove(board: List<Int>, turn: Int): Pair<Int, Int>? {
        for (from in board.indices) {
            if (board[from] == 0 || board[from] * turn <= 0) continue
            val moves = generatePseudoMoves(board, from, turn)
            if (moves.isNotEmpty()) {
                return from to moves.first()
            }
        }
        return null
    }

    private fun generatePseudoMoves(board: List<Int>, from: Int, turn: Int): List<Int> {
        val piece = board[from]
        if (piece == 0 || piece * turn <= 0) return emptyList()
        val results = mutableListOf<Int>()
        val row = from / 8
        val col = from % 8
        val direction = if (turn == 1) 1 else -1

        when (kotlin.math.abs(piece)) {
            1 -> {
                val forward = from + direction * 8
                if (forward in 0..63 && row + direction in 0..7 && board[forward] == 0) {
                    results.add(forward)
                    val startRow = if (turn == 1) 6 else 1
                    if (row == startRow) {
                        val double = from + direction * 16
                        if (board[double] == 0) results.add(double)
                    }
                }
                for (offset in listOf(-1, 1)) {
                    val targetCol = col + offset
                    if (targetCol !in 0..7) continue
                    val target = from + direction * 8 + offset
                    if (target !in 0..63) continue
                    if (board[target] != 0 && board[target] * turn < 0) results.add(target)
                }
            }
            2 -> {
                listOf(-17, -15, -10, -6, 6, 10, 15, 17).forEach { delta ->
                    val target = from + delta
                    if (target in 0..63) {
                        val targetRow = target / 8
                        val targetCol = target % 8
                        val rowDiff = kotlin.math.abs(targetRow - row)
                        val colDiff = kotlin.math.abs(targetCol - col)
                        if (rowDiff <= 2 && colDiff <= 2 && (rowDiff + colDiff == 3)) {
                            if (board[target] == 0 || board[target] * turn < 0) results.add(target)
                        }
                    }
                }
            }
        }

        return results
    }
}
