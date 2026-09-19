package so.keynan.golistracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val text = messages.joinToString(" ") { it.messageBody ?: "" }

        if (SmsParser.looksLikeSupportedGolisMessage(text)) {
            // Prototype behavior: parsing is intentionally local/read-only.
            // A production version should persist to an encrypted local database
            // and show a notification without exposing the SMS contents elsewhere.
            Toast.makeText(context, "Golis Tracker: new transaction detected", Toast.LENGTH_SHORT).show()
        }
    }
}
