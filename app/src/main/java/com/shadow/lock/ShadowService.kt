
package com.shadow.lock

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.View
import android.widget.*
import android.text.InputType
import java.security.MessageDigest

class ShadowService : Service() {

    private lateinit var windowManager: WindowManager
    private var popup: View? = null
    private var entered = ""

    companion object {
        private const val CHANNEL_ID = "shadow_service"
        private const val NOTIFICATION_ID = 1001
        private const val STOP_ACTION = "com.shadow.lock.STOP"
        private const val PIN_HASH =
            "0346642d6bd4724ed5dfe66b4ad5adda1eef84c6929cd113ebdbc0b3459ced9a"
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        showPopup()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_ACTION) {
            stopShadow()
            return START_NOT_STICKY
        }

        if (popup == null) showPopup()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SHADOW Protection",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, ShadowService::class.java).apply {
            action = STOP_ACTION
        }

        val pendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("SHADOW يعمل")
            .setContentText("النافذة العائمة نشطة")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .addAction(
                Notification.Action.Builder(
                    null,
                    "إيقاف الحماية",
                    pendingIntent
                ).build()
            )
            .build()
    }

    private fun showPopup() {
        if (popup != null) return

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 24, 28, 24)
            setBackgroundColor(Color.rgb(5, 10, 8))
        }

        val title = TextView(this).apply {
            text = "SHADOW"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(57, 255, 120))
        }

        val subtitle = TextView(this).apply {
            text = "أدخل كلمة المرور"
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(0, 18, 0, 18)
        }

        val password = EditText(this).apply {
            inputType =
                InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD
            setTextColor(Color.rgb(57, 255, 120))
            setHint("••••••")
            setHintTextColor(Color.GRAY)
            gravity = Gravity.CENTER
        }

        val status = TextView(this).apply {
            text = ""
            gravity = Gravity.CENTER
            setTextColor(Color.RED)
        }

        val unlock = Button(this).apply {
            text = "تأكيد"
            setOnClickListener {
                val digest = MessageDigest.getInstance("SHA-256")
                    .digest(
                        "SHADOW-v1-local-salt${password.text}".toByteArray()
                    )
                    .joinToString("") { "%02x".format(it) }

                if (digest == PIN_HASH) {
                    stopShadow()
                } else {
                    status.text = "كلمة المرور غير صحيحة"
                    password.text.clear()
                }
            }
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(
            password,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        root.addView(unlock)
        root.addView(status)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            LayoutParams.TYPE_PHONE
        }

        val params = LayoutParams(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LayoutParams.WRAP_CONTENT,
            type,
            LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        popup = root

        try {
            windowManager.addView(root, params)
        } catch (e: Exception) {
            popup = null
            stopSelf()
        }
    }

    private fun stopShadow() {
        popup?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        popup = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        popup?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        popup = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
