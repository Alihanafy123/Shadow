package com.shadow.lock

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private var entered = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 12, 8)
        window.navigationBarColor = Color.BLACK

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(Color.rgb(5, 10, 8))
        }

        val title = TextView(this).apply {
            text = "SHADOW"
            textSize = 34f
            setTextColor(Color.rgb(57, 255, 120))
            gravity = Gravity.CENTER
        }
        val subtitle = TextView(this).apply {
            text = "أدخل كلمة المرور"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 22, 0, 18)
        }
        val dots = TextView(this).apply {
            text = "○  ○  ○  ○  ○  ○"
            textSize = 24f
            setTextColor(Color.rgb(57, 255, 120))
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }
        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(subtitle, LinearLayout.LayoutParams(-1, -2))
        root.addView(dots, LinearLayout.LayoutParams(-1, -2))

        val grid = GridLayout(this).apply {
            columnCount = 3
            rowCount = 4
            alignmentMode = GridLayout.ALIGN_BOUNDS
        }
        val keys = listOf("1","2","3","4","5","6","7","8","9","⌫","0","✓")
        keys.forEach { key ->
            val button = Button(this).apply {
                text = key
                textSize = 22f
                setTextColor(Color.rgb(57, 255, 120))
                setBackgroundColor(Color.rgb(16, 28, 20))
                setOnClickListener {
                    when (key) {
                        "⌫" -> if (entered.isNotEmpty()) entered = entered.dropLast(1)
                        "✓" -> Toast.makeText(
                            this@MainActivity,
                            "واجهة تجريبية فقط — لم يتم تفعيل التحقق من كلمة المرور",
                            Toast.LENGTH_LONG
                        ).show()
                        else -> if (entered.length < 6) entered += key
                    }
                    dots.text = (0 until 6).joinToString("  ") {
                        if (it < entered.length) "●" else "○"
                    }
                }
            }
            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = 68
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(5, 5, 5, 5)
            }
            grid.addView(button, lp)
        }
        root.addView(grid, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        val footer = TextView(this).apply {
            text = "PRIVATE ACCESS • SHADOW"
            textSize = 11f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 0)
        }
        root.addView(footer)
        setContentView(root)
    }
}
