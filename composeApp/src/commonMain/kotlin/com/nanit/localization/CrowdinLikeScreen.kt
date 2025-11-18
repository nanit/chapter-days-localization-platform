package com.nanit.localization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Main Crowdin-like localization management screen
 * Allows viewing and editing string keys, descriptions, and translations for multiple languages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdinLikeScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val translationsList = remember { mutableStateListOf<TranslationDomain>() }
    var callbackTriggered by remember { mutableIntStateOf(0) }

//    val str by MockData._logs.collectAsStateWithLifecycle()
    val logs = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        LoggerStringsProvider._state
            .onEach {
                println("New values: $it")
            }
            .map { (mes,_) -> mes.toList() }
            .collect {
                logs.clear()
                logs.addAll(it)
            }
    }
    // Load data from MockData
    LaunchedEffect(Unit) {
        MockData.registerObse("CrowdinLikeScreen") {
            callbackTriggered += 1
            translationsList.clear()
            translationsList.addAll(MockData.getAll())
        }
        translationsList.addAll(MockData.getAll())
    }

    // Filter translations based on search query
    val filteredTranslations = remember(searchQuery, callbackTriggered) {
        if (searchQuery.isBlank()) {
            translationsList
        } else {
            translationsList.filter { translation ->
                translation.key.contains(searchQuery, ignoreCase = true) ||
                translation.desc?.contains(searchQuery, ignoreCase = true) == true ||
                translation.values.any { it.value.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Scaffold(
        topBar = {
//            TopAppBar(
//                title = { Text("Localization Manager") },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add new string key */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add new key")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = logs.joinToString("\n")
            )
            // Search Bar
            SearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filteredTranslations.size} strings",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${getAllLanguages(translationsList).size} languages",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Translations List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredTranslations,
                    key = { it.key }
                ) { translation ->
                    TranslationCard(
                        translation = translation,
                        onSave = { key, locale, value ->
                            MockData.put(key, locale, value)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Search field component
 */
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search keys, descriptions, or translations...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Card component for a single translation key with all its languages
 */
@Composable
private fun TranslationCard(
    translation: TranslationDomain,
    onSave: (key: String, locale: CombinedLocale, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Key name and expand/collapse button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = translation.key,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (translation.desc != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = translation.desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }

            // Translation count badge
            if (!isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${translation.values.size} translations",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Expanded content: All translations
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider()

                    translation.values.sortedBy { it.locale.value }.forEach { stuff ->
                        TranslationRow(
                            stuff = stuff,
                            onSave = { newValue ->
                                onSave(stuff.parentKey, stuff.locale, newValue)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Row component for a single translation in a specific language
 */
@Composable
private fun TranslationRow(
    stuff: Stuff,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editedValue by remember(stuff.value) { mutableStateOf(stuff.value) }
    var isEditing by remember { mutableStateOf(false) }
    val hasChanges = editedValue != stuff.value

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language code badge
            Text(
                text = stuff.locale.value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )

            if (!isEditing) {
                IconButton(
                    onClick = { isEditing = true }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Translation value (editable or read-only)
        if (isEditing) {
            OutlinedTextField(
                value = editedValue,
                onValueChange = { editedValue = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 5,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = {
                        editedValue = stuff.value
                        isEditing = false
                    }
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        onSave(editedValue)
                        isEditing = false
                    },
                    enabled = hasChanges
                ) {
                    Text("Save")
                }
            }
        } else {
            Text(
                text = editedValue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Helper function to get all unique languages from translations
 */
private fun getAllLanguages(translations: List<TranslationDomain>): Set<CombinedLocale> {
    return translations.flatMap { it.values.map { stuff -> stuff.locale } }.toSet()
}

@Preview
@Composable
private fun CrowdinLikeScreenPreview() {
    MaterialTheme {
        CrowdinLikeScreen()
    }
}

@Preview
@Composable
private fun TranslationCardPreview() {
    MaterialTheme {
        TranslationCard(
            translation = TranslationDomain(
                key = "login_btn_sign_in",
                desc = "Button used in Login Screen for Log In purpose",
                values = setOf(
                    Stuff(
                        parentKey = "login_btn_sign_in",
                        locale = CombinedLocale("en", "us"),
                        value = "Log In"
                    ),
                    Stuff(
                        parentKey = "login_btn_sign_in",
                        locale = CombinedLocale("ua", "ua"),
                        value = "Логін"
                    )
                )
            ),
            onSave = { _, _, _ -> }
        )
    }
}
