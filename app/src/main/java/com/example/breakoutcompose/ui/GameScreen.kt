package com.example.breakoutcompose.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun GameScreen() {
    var ballPosition by remember { mutableStateOf(Offset(300f, 700f)) }
    var ballVelocity by remember { mutableStateOf(Offset(5f, -5f)) }
    var paddleX by remember { mutableStateOf(250f) }
    val paddleWidth = 200f
    val paddleHeight = 20f
    val screenWidth = 800f
    val screenHeight = 1500f
    var bricks by remember {
        mutableStateOf(
            List(5) { row ->
                List(6) { col ->
                    Rect(Offset(col * 120f + 20f, row * 60f + 40f), Size(100f, 40f))
                }
            }.flatten()
        )
    }
    var gameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (!gameOver) {
            delay(16)
            val nextBall = ballPosition + ballVelocity
            var newVelocity = ballVelocity.copy()

            if (nextBall.x < 0 || nextBall.x > screenWidth) newVelocity = newVelocity.copy(x = -newVelocity.x)
            if (nextBall.y < 0) newVelocity = newVelocity.copy(y = -newVelocity.y)

            val paddleRect = Rect(Offset(paddleX, screenHeight - 100f), Size(paddleWidth, paddleHeight))
            if (paddleRect.contains(nextBall)) {
                newVelocity = newVelocity.copy(y = -abs(newVelocity.y))
            }

            val hitBrick = bricks.firstOrNull { it.contains(nextBall) }
            if (hitBrick != null) {
                bricks = bricks - hitBrick
                newVelocity = newVelocity.copy(y = -newVelocity.y)
                score += 10
            }

            if (nextBall.y > screenHeight) {
                gameOver = true
            }

            ballVelocity = newVelocity
            ballPosition += newVelocity
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Score: $score", style = MaterialTheme.typography.headlineSmall)

        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    paddleX += dragAmount.x
                    paddleX = paddleX.coerceIn(0f, screenWidth - paddleWidth)
                }
            }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(Color.Red, 20f, center = ballPosition)
                drawRect(Color.Blue, Offset(paddleX, screenHeight - 100f), Size(paddleWidth, paddleHeight))
                for (brick in bricks) {
                    drawRect(Color.Green, brick.topLeft, brick.size)
                }
            }

            if (gameOver) {
                Button(
                    onClick = {
                        ballPosition = Offset(300f, 700f)
                        ballVelocity = Offset(5f, -5f)
                        bricks = List(5) { row ->
                            List(6) { col ->
                                Rect(Offset(col * 120f + 20f, row * 60f + 40f), Size(100f, 40f))
                            }
                        }.flatten()
                        score = 0
                        gameOver = false
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("重新開始")
                }
            }
        }
    }
}
