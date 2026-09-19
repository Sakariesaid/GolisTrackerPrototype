package so.keynan.golistracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private lateinit var balanceText: TextView
    private lateinit var todayIncome: TextView
    private lateinit var countText: TextView
    private lateinit var transactionsText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        balanceText = findViewById(R.id.balanceText)
        todayIncome = findViewById(R.id.todayIncome)
        countText = findViewById(R.id.countText)
        transactionsText = findViewById(R.id.transactionsText)
        statusText = findViewById(R.id.statusText)

        findViewById<Button>(R.id.permissionButton).setOnClickListener {
            requestSmsPermissions()
        }

        if (hasSmsPermission()) {
            statusText.text = "SMS access enabled"
            loadExistingSms()
        }
    }

    private fun hasSmsPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) ==
            PackageManager.PERMISSION_GRANTED

    private fun requestSmsPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.POST_NOTIFICATIONS
            ),
            100
        )
    }

    private fun loadExistingSms() {
        val result = SmsParser.readSupportedMessages(this)
        if (result.isEmpty()) return

        val newest = result.first()
        balanceText.text = newest.balance?.let { "$${"%.2f".format(it)}" } ?: "—"
        todayIncome.text = "Latest income\n$${"%.2f".format(result.sumOf { it.amount ?: 0.0 })}"
        countText.text = "Transactions\n${result.size}"

        transactionsText.text = result.take(10).joinToString("\n\n") {
            "↑ +$${"%.2f".format(it.amount ?: 0.0)}  ${it.reference ?: "No reference"}\n${it.timeText}"
        }
        statusText.text = "Last scan: ${result.size} supported SMS message(s)"
    }
}
