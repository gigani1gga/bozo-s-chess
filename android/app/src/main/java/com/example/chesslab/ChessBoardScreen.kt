package com.example.chesslab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChessBoardScreen() {
    val engine = remember { ChessEngine() }
    val boardState = remember { ChessBoardState() }
    var selectedSquare by remember { mutableStateOf<Int?>(null) }
    var status by remember { mutableStateOf("White to move") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Chess Lab",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFF1E1E2E), shape = RoundedCornerShape(18.dp))
                .padding(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) {
                items(64) { index ->
                    val row = index / 8
                    val col = index % 8
                    val isLight = (row + col) % 2 == 0
                    val piece = boardState.board[index]
                    val legal = selectedSquare != null && boardState.legalMovesFor(selectedSquare!!).contains(index)

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(
                                if (isLight) Color(0xFFE7D6A9) else Color(0xFF7E8F5C)
                            )
                            .clickable {
                                val from = selectedSquare
                                if (from == null) {
                                    if (piece != 0) {
                                        selectedSquare = index
                                        status = "Selected ${pieceName(piece)}"
                                    }
                                } else {
                                    if (from == index) {
                                        selectedSquare = null
                                        status = "White to move"
                                    } else if (boardState.isLegalMove(from, index)) {
                                        boardState.move(from, index)
                                        selectedSquare = null
                                        status = "Moved ${pieceName(piece)}"
                                    } else {
                                        selectedSquare = index
                                        status = "Selected ${pieceName(boardState.board[index])}"
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (legal) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color(0x66000000), shape = RoundedCornerShape(50))
                            )
                        }

                        if (piece != 0) {
                            Text(
                                text = pieceToUnicode(piece),
                                fontSize = 28.sp,
                                color = if (piece > 0) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                boardState.resetBoard()
                selectedSquare = null
                status = "White to move"
            }) {
                Text("Reset")
            }

            Button(onClick = {
                val bestMove = engine.bestMove(boardState.board, boardState.turn)
                if (bestMove != null) {
                    boardState.move(bestMove.first, bestMove.second)
                    status = "Engine moved"
                } else {
                    status = "Native engine not connected yet"
                }
            }) {
                Text("Engine")
            }
        }
    }
}

private fun pieceName(code: Int): String {
    return when (kotlin.math.abs(code)) {
        1 -> "pawn"
        2 -> "knight"
        3 -> "bishop"
        4 -> "rook"
        5 -> "queen"
        6 -> "king"
        else -> "empty"
    }
}

private fun pieceToUnicode(piece: Int): String {
    return when (piece) {
        1 -> "♙"
        2 -> "♘"
        3 -> "♗"
        4 -> "♖"
        5 -> "♕"
        6 -> "♔"
        -1 -> "♟"
        -2 -> "♞"
        -3 -> "♝"
        -4 -> "♜"
        -5 -> "♛"
        -6 -> "♚"
        else -> ""
    }
}
