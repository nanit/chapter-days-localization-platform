package com.nanit.localization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localizationManager.NanitLocalization

/**
 * Demo screen to test LocalizationManager.
 * Uses NanitLocalization singleton to access localized strings.
 *
 * Can be used with:
 * - Fake LocalizationManager for testing
 * - Real LocalizationManager for production
 *
 * Just initialize NanitLocalization before using this screen.
 */
@Composable
fun LocalizationTestScreen(
    onLocaleChange: ((String) -> Unit)? = null
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Localization Test Screen",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider()

            // Display current locale
            val currentLocale by NanitLocalization.getManager().currentLocale.collectAsState()
            Text(
                text = "Current Locale: ${currentLocale.languageCode}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider()

            // Display localized strings
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Localized Strings:",
                        style = MaterialTheme.typography.titleSmall
                    )

                    LocalizedStringRow("welcome_message")
                    LocalizedStringRow("greeting")
                    LocalizedStringRow("login_button")
                    LocalizedStringRow("settings_title")
                    LocalizedStringRow("logout_button")
                    LocalizedStringRow("goodbye")
                }
            }

            HorizontalDivider()

            // Language selector buttons (only show if callback provided)
            if (onLocaleChange != null) {
                Text(
                    text = "Select Language:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocaleButton("EN", "en", onLocaleChange, Modifier.weight(1f))
                    LocaleButton("ES", "es", onLocaleChange, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocaleButton("FR", "fr", onLocaleChange, Modifier.weight(1f))
                    LocaleButton("HE", "he", onLocaleChange, Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocaleButton("DE", "de", onLocaleChange, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LocalizedStringRow(key: String) {
    // Use NanitLocalization.stringResource directly
    val value = NanitLocalization.stringResource(key)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$key:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun LocaleButton(
    label: String,
    localeCode: String,
    onLocaleChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onLocaleChange(localeCode) },
        modifier = modifier
    ) {
        Text(label)
    }
}