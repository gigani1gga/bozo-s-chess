package com.example.chesslab

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

class ChessBoardState {
    val board = mutableStateListOf<Int>().apply {
        addAll(initialBoard())
    }

    var turn = 1
        private set

    fun resetBoard() {
        board.clear()
        board.addAll(initialBoard())
        turn = 1
    }

    fun move(from: Int, to: Int) {
        if (from !in 0..63 || to !in 0..63) return
        val piece = board[from]
        if (piece == 0) return
        if (piece * turn <= 0) return
        if (!isLegalMove(from, to)) return

        board[to] = piece
        board[from] = 0
        turn *= -1
    }

    fun isLegalMove(from: Int, to: Int): Boolean {
        val piece = board[from]
        if (piece == 0) return false
        if (board[to] != 0 && board[to] * piece > 0) return false
        if (piece * turn <= 0) return false
        return legalMovesFor(from).contains(to)
    }

    fun legalMovesFor(from: Int): List<Int> {
        val piece = board[from]
        if (piece == 0) return emptyList()
        val moves = mutableListOf<Int>()
        val row = from / 8
        val col = from % 8
        val direction = if (piece > 0) 1 else -1

        when (kotlin.math.abs(piece)) {
            1 -> {
                val forward = from + direction * 8
                if (forward in 0..63 && row + direction in 0..7 && board[forward] == 0) {
                    moves.add(forward)
                    val startRow = if (piece > 0) 6 else 1
                    if (row == startRow) {
                        val twoStep = from + direction * 16
                        if (twoStep in 0..63 && board[twoStep] == 0) moves.add(twoStep)
                    }
                }
                for (offset in listOf(-1, 1)) {
                    val targetCol = col + offset
                    if (targetCol !in 0..7) continue
                    val target = from + direction * 8 + offset
                    if (target !in 0..63) continue
                    if (board[target] != 0 && board[target] * piece < 0) moves.add(target)
                }
            }
            2 -> {
                val knights = listOf(
                    -17, -15, -10, -6, 6, 10, 15, 17
                )
                for (delta in knights) {
                    val target = from + delta
                    val currentRow = from / 8
                    val currentCol = from % 8
                    val targetRow = target / 8
                    val targetCol = target % 8
                    if (target !in 0..63) continue
                    if (kotlin.math.abs(targetRow - currentRow) > 2) continue
                    if (kotlin.math.abs(targetCol - currentCol) > 2) continue
                    if (board[target] == 0 || board[target] * piece < 0) moves.add(target)
                }
            }
            3 -> slideMoves(from, listOf(-9, -7, 7, 9), moves)
            4 -> slideMoves(from, listOf(-8, -1, 1, 8), moves)
            5 -> {
                slideMoves(from, listOf(-9, -7, 7, 9), moves)
                slideMoves(from, listOf(-8, -1, 1, 8), moves)
            }
            6 -> {
                for (delta in listOf(-9, -8, -7, -1, 1, 7, 8, 9)) {
                    val target = from + delta
                    if (target !in 0..63) continue
                    val r = target / 8
                    val c = target % 8
                    if (kotlin.math.abs(r - row) > 1 || kotlin.math.abs(c - col) > 1) continue
                    if (board[target] == 0 || board[target] * piece < 0) moves.add(target)
                }
            }
        }

        return moves.filter { it in 0..63 }
    }

    private fun slideMoves(from: Int, directions: List<Int>, moves: MutableList<Int>) {
        val piece = board[from]
        val row = from / 8
        val col = from % 8

        for (direction in directions) {
            var current = from + direction
            while (current in 0..63) {
                val currentRow = current / 8
                val currentCol = current % 8
                if (kotlin.math.abs(currentRow - row) > 1 && direction in listOf(-9, -7, 7, 9) ||
                    kotlin.math.abs(currentCol - col) > 1 && direction in listOf(-8, -1, 1, 8)
                ) {
                    break
                }
                if (kotlin.math.abs(currentRow - row) > 2 || kotlin.math.abs(currentCol - col) > 2) {
                    break
                }
                val targetPiece = board[current]
                if (targetPiece == 0) {
                    moves.add(current)
                } else {
                    if (targetPiece * piece < 0) moves.add(current)
                    break
                }
                current += direction
            }
        }
    }

    private fun initialBoard(): List<Int> {
        return listOf(
            4, 2, 3, 5, 6, 3, 2, 4,
            1, 1, 1, 1, 1, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            -1, -1, -1, -1, -1, -1, -1, -1,
            -4, -2, -3, -5, -6, -3, -2, -4
        )
    }
}
