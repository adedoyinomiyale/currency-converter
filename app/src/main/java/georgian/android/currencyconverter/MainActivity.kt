package georgian.android.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import georgian.android.currencyconverter.R.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val inputAmount = findViewById<EditText>(id.inputAmount)
        val convertButton = findViewById<Button>(id.convertButton)
        val resultText = findViewById<TextView>(id.resultText)
        val currencySpinner: Spinner = findViewById(id.currencySpinner)
        val currencies = arrayOf("USD", "EUR", "GBP")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencySpinner.adapter = adapter

        convertButton.setOnClickListener {
            val amount = inputAmount.text.toString().toDoubleOrNull()
            if (amount != null) {
                val selectedCurrency = currencySpinner.selectedItem.toString()
                GlobalScope.launch(Dispatchers.IO) {
                    val convertedAmount = getConvertedAmount(amount, selectedCurrency)
                    launch(Dispatchers.Main) {
                        resultText.text = "Converted Amount: $convertedAmount"
                    }
                }
            } else {
                resultText.text = "Please enter a valid amount"
            }
        }
    }

    private fun getConvertedAmount(amount: Double, currency: String): Double {
        val apiUrl = "https://v6.exchangerate-api.com/v6/bce24061fc84e906939c507b/latest/USD"

        // Make an API call to fetch exchange rates and calculate the converted amount
        val jsonResponse = URL(apiUrl).readText()
        val exchangeRates = JSONObject(jsonResponse).getJSONObject("rates")

        return when (currency) {
            "USD" -> amount // No conversion needed for USD
            "EUR" -> amount * exchangeRates.getDouble("EUR")
            "GBP" -> amount * exchangeRates.getDouble("GBP")
            "NGN" -> amount * exchangeRates.getDouble("NGN")
            else -> amount // Default to original amount if currency not recognized
        }
    }
}
