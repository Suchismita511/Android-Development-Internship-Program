package com.example.calculator

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    private var currentExpression = ""
    private var isCalculated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)
    }

    private fun setupListeners() {

        val numberButtons = mapOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9"
        )

        numberButtons.forEach { (id, digit) ->
            findViewById<CardView>(id).setOnClickListener {
                appendDigit(digit)
            }
        }


        findViewById<CardView>(R.id.btnPlus).setOnClickListener { appendOperator("+") }
        findViewById<CardView>(R.id.btnMinus).setOnClickListener { appendOperator("−") }
        findViewById<CardView>(R.id.btnMultiply).setOnClickListener { appendOperator("×") }
        findViewById<CardView>(R.id.btnDivide).setOnClickListener { appendOperator("÷") }
        findViewById<CardView>(R.id.btnPercent).setOnClickListener { applyPercent() }


        findViewById<CardView>(R.id.btnDot).setOnClickListener { appendDot() }


        findViewById<CardView>(R.id.btnPlusMinus).setOnClickListener { toggleSign() }

        findViewById<CardView>(R.id.btnAC).setOnClickListener { clearAll() }
        findViewById<CardView>(R.id.btnBackspace).setOnClickListener { backspace() }

        findViewById<CardView>(R.id.btnEquals).setOnClickListener { calculateFinalResult() }
    }

    private fun appendDigit(digit: String) {
        if (isCalculated) {
            currentExpression = ""
            isCalculated = false
        }
        currentExpression += digit
        updateDisplay()
        autoPreview()
    }

    private fun appendOperator(op: String) {
        if (currentExpression.isEmpty()) {
            if (op == "−") {
                currentExpression = "−"
                updateDisplay()
            }
            return
        }

        val lastChar = currentExpression.last()
        if (isOperator(lastChar.toString())) {
            currentExpression = currentExpression.dropLast(1) + op
        } else {
            currentExpression += op
        }

        isCalculated = false
        updateDisplay()
    }

    private fun appendDot() {
        if (isCalculated) {
            currentExpression = "0"
            isCalculated = false
        }

        val tokens = currentExpression.split(Regex("[+\\-−×÷]"))
        val currentNum = tokens.lastOrNull() ?: ""

        if (!currentNum.contains(".")) {
            currentExpression += if (currentNum.isEmpty()) "0." else "."
            updateDisplay()
        }
    }

    private fun applyPercent() {
        if (currentExpression.isEmpty() || isOperator(currentExpression.last().toString())) return

        try {
            val result = evaluateExpression(currentExpression) / 100.0
            currentExpression = formatResult(result)
            tvResult.text = currentExpression
            tvExpression.text = ""
            isCalculated = true
        } catch (_: Exception) {
            tvResult.text = "Error"
        }
    }

    private fun toggleSign() {
        if (currentExpression.isEmpty()) return


        val operatorRegex = Regex("([+\\-−×÷])")
        val parts = currentExpression.split(operatorRegex).filter { it.isNotEmpty() }
        if (parts.isNotEmpty()) {
            val lastNum = parts.last()
            val beforeNum = currentExpression.dropLast(lastNum.length)

            if (beforeNum.endsWith("−")) {
                currentExpression = beforeNum.dropLast(1) + "+" + lastNum
            } else if (beforeNum.endsWith("+")) {
                currentExpression = beforeNum.dropLast(1) + "−" + lastNum
            } else if (beforeNum.isEmpty()) {
                currentExpression = "−$lastNum"
            } else {
                currentExpression = "$beforeNum−$lastNum"
            }
            updateDisplay()
            autoPreview()
        }
    }

    private fun backspace() {
        if (currentExpression.isNotEmpty()) {
            currentExpression = currentExpression.dropLast(1)
            updateDisplay()
            if (currentExpression.isEmpty()) {
                tvResult.text = ""
            } else {
                autoPreview()
            }
        }
    }

    private fun clearAll() {
        currentExpression = ""
        tvExpression.text = ""
        tvResult.text = ""
        isCalculated = false
    }

    private fun autoPreview() {
        val lastChar = currentExpression.lastOrNull()?.toString() ?: ""
        if (isOperator(lastChar)) return

        try {
            val res = evaluateExpression(currentExpression)
            tvResult.text = formatResult(res)
        } catch (_: Exception) {

        }
    }

    private fun calculateFinalResult() {
        if (currentExpression.isEmpty()) return

        try {
            val res = evaluateExpression(currentExpression)
            tvExpression.text = currentExpression
            val formatted = formatResult(res)
            tvResult.text = formatted
            currentExpression = formatted
            isCalculated = true
        } catch (_: Exception) {
            tvResult.text = "Error"
        }
    }

    private fun updateDisplay() {
        tvExpression.text = currentExpression
    }

    private fun isOperator(s: String): Boolean {
        return s in listOf("+", "−", "-", "×", "÷")
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }


    private fun evaluateExpression(expr: String): Double {
        val normalized = expr.replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        val tokens = mutableListOf<String>()
        var numberBuffer = StringBuilder()

        var i = 0
        while (i < normalized.length) {
            val ch = normalized[i]

            if (ch.isDigit() || ch == '.') {
                numberBuffer.append(ch)
            } else if (ch in listOf('+', '-', '*', '/')) {

                if (ch == '-' && (tokens.isEmpty() && numberBuffer.isEmpty())) {
                    numberBuffer.append(ch)
                } else {
                    if (numberBuffer.isNotEmpty()) {
                        tokens.add(numberBuffer.toString())
                        numberBuffer = StringBuilder()
                    }
                    tokens.add(ch.toString())
                }
            }
            i++
        }
        if (numberBuffer.isNotEmpty()) {
            tokens.add(numberBuffer.toString())
        }

        if (tokens.isEmpty()) return 0.0


        val intermediateTokens = mutableListOf<String>()
        var idx = 0
        while (idx < tokens.size) {
            val token = tokens[idx]
            if (token == "*" || token == "/") {
                val prevNum = intermediateTokens.removeAt(intermediateTokens.size - 1).toDouble()
                val nextNum = tokens[idx + 1].toDouble()
                val result = if (token == "*") prevNum * nextNum else prevNum / nextNum
                intermediateTokens.add(result.toString())
                idx += 2
            } else {
                intermediateTokens.add(token)
                idx++
            }
        }


        var total = intermediateTokens[0].toDouble()
        var opIdx = 1
        while (opIdx < intermediateTokens.size) {
            val op = intermediateTokens[opIdx]
            val nextVal = intermediateTokens[opIdx + 1].toDouble()
            if (op == "+") {
                total += nextVal
            } else if (op == "-") {
                total -= nextVal
            }
            opIdx += 2
        }

        return total
    }
}