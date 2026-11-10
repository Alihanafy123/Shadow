
package com.shadow.lock

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.graphics.Color

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(30, 30, 30, 30)
            setBackgroundColor(Color.rgb(5, 10, 8))
        }

        val title = TextView(this).apply {
            text = "SHADOW"
            textSize = 34f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(57, 255, 120))
        }

        val info = TextView(this).apply {
            text = "يحتاج SHADOW إلى صلاحية الظهور فوق التطبيقات"
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 25, 0, 25)
        }

        val button = Button(this).apply {
            text = "تفعيل النافذة العائمة"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                } else {
                    startShadow()
                }
            }
        }

        root.addView(title)
        root.addView(info)
        root.addView(button)
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            startShadow()
        }
    }

    private fun startShadow() {
        val intent = Intent(this, ShadowService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
