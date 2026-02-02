package com.example.helloapp

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ReminderActivity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnDate = findViewById<Button>(R.id.btnPickDate)
        val btnTime = findViewById<Button>(R.id.btnPickTime)
        val btnSet = findViewById<Button>(R.id.btnSetReminder)

        btnDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, y, m, d -> calendar.set(y, m, d) },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnTime.setOnClickListener {
            TimePickerDialog(
                this,
                { _, h, min ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, min)
                    calendar.set(Calendar.SECOND, 0)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()
        }

        btnSet.setOnClickListener {
            val message = etMessage.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setAlarm(calendar.timeInMillis, message)
            Toast.makeText(this, "Reminder set", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setAlarm(time: Long, message: String) {

        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("MESSAGE", message)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Pick a future time ⏰", Toast.LENGTH_SHORT).show()
            return
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // 🔥 THIS is what registers your app in Realme's alarm list
                startActivity(
                    Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                )
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

    }

}
