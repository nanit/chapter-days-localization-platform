package com.nanit.localization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localizationManager.NanitLocalization
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

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
    var showPerformanceTest by remember { mutableStateOf(false) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Localization Test Screen",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Display current locale
            val currentLocale by NanitLocalization.getManager().currentLocale.collectAsState()
            Text(
                text = "Current Locale: ${currentLocale.languageCode}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Performance test toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show 1000 strings (Performance Test)",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = showPerformanceTest,
                    onCheckedChange = { showPerformanceTest = it }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (showPerformanceTest) {
                PerformanceTestView()
            } else {
                DefaultTestView(onLocaleChange)
            }
        }
    }
}

@Composable
private fun ColumnScope.DefaultTestView(onLocaleChange: ((String) -> Unit)?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

@OptIn(ExperimentalTime::class)
@Composable
private fun ColumnScope.PerformanceTestView() {
    var renderTime by remember { mutableStateOf<Long?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Generate list of all keys
    val keys = remember { List(1000) { "key_$it" } }

    // Measure initial render time
    LaunchedEffect(Unit) {
        val startTime = Clock.System.now().toEpochMilliseconds()
        delay(100) // Give time for initial composition
        renderTime = Clock.System.now().toEpochMilliseconds() - startTime
        isLoading = false
    }

    // Performance metrics card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Performance Metrics",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Total strings: ${keys.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = if (isLoading) "Measuring..." else "Load time: ${renderTime}ms",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    // Scrollable list with all strings
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Text(
                    text = "All Localized Strings (Scroll to test performance):",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(keys) { key ->
                LocalizedStringRow(key)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 2.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
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
