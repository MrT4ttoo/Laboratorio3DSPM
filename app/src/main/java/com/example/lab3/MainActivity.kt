package com.example.lab3

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var secretNumber = (0..100).random()
    private var attemptsLeft = 3
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val input = findViewById<EditText>(R.id.inputNumber)
        val guessBtn = findViewById<Button>(R.id.guessButton)
        val restartBtn = findViewById<Button>(R.id.restartButton)
        val feedback = findViewById<TextView>(R.id.feedback)
        val attempts = findViewById<TextView>(R.id.attemptsLeft)
        val countdown = findViewById<TextView>(R.id.countdown)

        attempts.text = "Intentos restantes: $attemptsLeft"
        restartBtn.visibility = View.GONE // Ocultamos el botón al inicio

        timer = createTimer(countdown).start()

        guessBtn.setOnClickListener {
            val guess = input.text.toString().toIntOrNull()

            if (guess == null || guess !in 0..100) {
                Toast.makeText(this, "Ingresa un número válido (0-100)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            attemptsLeft--
            attempts.text = "Intentos restantes: $attemptsLeft"

            when {
                guess == secretNumber -> {
                    disableGame("¡Correcto! Ganaste.")
                }
                attemptsLeft == 0 -> {
                    disableGame("¡Perdiste! El número era $secretNumber")
                }
                guess < secretNumber -> {
                    feedback.text = "El número es mayor"
                }
                else -> {
                    feedback.text = "El número es menor"
                }
            }
        }

        restartBtn.setOnClickListener {
            secretNumber = (0..100).random()
            attemptsLeft = 3
            feedback.text = ""
            input.text.clear()
            input.isEnabled = true
            guessBtn.isEnabled = true
            attempts.text = "Intentos restantes: $attemptsLeft"
            restartBtn.visibility = View.GONE // Ocultamos de nuevo al reiniciar
            timer.cancel()
            timer = createTimer(countdown).start()
        }
    }

    private fun disableGame(message: String) {
        timer.cancel()
        findViewById<Button>(R.id.guessButton).isEnabled = false
        findViewById<EditText>(R.id.inputNumber).isEnabled = false
        findViewById<TextView>(R.id.feedback).text = message
        findViewById<Button>(R.id.restartButton).visibility = View.VISIBLE // Mostramos botón
    }

    private fun createTimer(countdown: TextView): CountDownTimer {
        return object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdown.text = "Tiempo restante: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                disableGame("Tiempo agotado. El número era $secretNumber")
            }
        }
    }
}
