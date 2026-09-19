package so.keynan.golistracker

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

data class ParsedSms(
    val amount: Double?,
    val balance: Double?,
    val reference: String?,
    val timeText: String
)

object SmsParser {

    private val amountPattern =
        Pattern.compile("""(?:received|credited|credit|you have received)\D{0,30}(?:\$)?\s*([0-9]+(?:\.[0-9]{1,2})?)""", Pattern.CASE_INSENSITIVE)

    private val balancePattern =
        Pattern.compile("""(?:balance|new balance)\D{0,20}(?:\$)?\s*([0-9]+(?:\.[0-9]{1,2})?)""", Pattern.CASE_INSENSITIVE)

    private val referencePattern =
        Pattern.compile("""(?:ref|reference|receipt)\D{0,10}([A-Z0-9#-]{5,})""", Pattern.CASE_INSENSITIVE)

    fun looksLikeSupportedGolisMessage(body: String): Boolean {
        val s = body.lowercase(Locale.US)
        return (s.contains("golis") || s.contains("sahal")) &&
                (s.contains("received") || s.contains("credited") || s.contains("balance"))
    }

    fun parse(body: String, timeMillis: Long): ParsedSms? {
        if (!looksLikeSupportedGolisMessage(body)) return null

        fun number(pattern: Pattern): Double? {
            val m = pattern.matcher(body)
            return if (m.find()) m.group(1)?.toDoubleOrNull() else null
        }

        val refMatcher = referencePattern.matcher(body)
        val ref = if (refMatcher.find()) refMatcher.group(1) else null

        return ParsedSms(
            amount = number(amountPattern),
            balance = number(balancePattern),
            reference = ref,
            timeText = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(timeMillis))
        )
    }

    fun readSupportedMessages(context: Context): List<ParsedSms> {
        if (context.checkSelfPermission(android.Manifest.permission.READ_SMS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) return emptyList()

        val result = mutableListOf<ParsedSms>()
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf(Telephony.Sms.BODY, Telephony.Sms.DATE),
            null, null,
            Telephony.Sms.DEFAULT_SORT_ORDER
        ) ?: return emptyList()

        cursor.use {
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            while (it.moveToNext() && result.size < 100) {
                val body = it.getString(bodyIndex) ?: continue
                val date = it.getLong(dateIndex)
                parse(body, date)?.let(result::add)
            }
        }
        return result
    }
}
