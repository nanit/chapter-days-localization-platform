package com.nanit.localization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.callbackFlow
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.jvm.JvmInline
import kotlin.toString

@Preview
@Composable
private fun ListItemPreview() {
    MaterialTheme {

    }
}

data class TranslationDomain(
    val key: String,
    val desc: String?,
    val values: List<Stuff>,
)

data class Stuff(
    val parentKey: String,
    val locale: CombinedLocale,
    val value: String,
)

@JvmInline
value class CombinedLocale(val value: String) {
    constructor(lang: String, region: String) : this("${lang.decap()}-${region.cap()}")

    val first: String get() = value.split("-")[0]

    companion object {
        private fun String.cap(): String = buildString {
            this@cap.forEach { char ->
                val new = if (char.isLowerCase()) char.titlecase() else char.toString()
                append(new)
            }
        }

        private fun String.decap(): String = buildString {
            this@decap.forEach { char ->
                val new = if (char.isTitleCase()) char.lowercase() else char.toString()
                append(new)
            }
        }
    }
}


@Preview
@Composable
private fun ValuesScreenPreview() {
    MaterialTheme {
        ValuesScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuesScreen(
    modifier: Modifier = Modifier
) {
    var searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )
    var mock = remember { mutableStateListOf(*MockData.getAll().toTypedArray()) }
    var callbackTriggered: Int by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        MockData.registerObse("ValuesScreen") {
            callbackTriggered += 1
            mock.clear()
            mock.addAll(MockData.getAll())
        }
    }
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildString {
                appendLine(callbackTriggered.toString())
                mock.forEach {
                    appendLine(it.toString())
                }
            }
        )
        var input by remember { mutableStateOf("") }
        // Controls expansion state of the search bar
        val textFieldState: TextFieldState = rememberTextFieldState()
        var expanded by rememberSaveable { mutableStateOf(false) }
        val searchResults: List<String> = listOf("1", "2", "3")
        Box(
            Modifier.semantics { isTraversalGroup = true }
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                inputField = {
                    SearchBarDefaults.InputField(
                        query = textFieldState.text.toString(),
                        onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                        onSearch = {
//                            onSearch(textFieldState.text.toString())
                            expanded = false
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = { Text("Search") }
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                // Display search results in a scrollable column
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    searchResults.forEach { result ->
                        ListItem(
                            headlineContent = { Text(result) },
                            modifier = Modifier
                                .clickable {
                                    textFieldState.edit { replace(0, length, result) }
                                    expanded = false
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }

        val greeting = remember { "Greeting().greet()" }
        Text("Compose: $greeting")
        StuffList(mock)

    }
}
@Composable
private fun StuffList(
    list: List<TranslationDomain>
) {
    LazyColumn {
        items(
            items = list,
            key = {
                it.key
            }
        ) {
            StuffListItem(it) {a,b,c ->
                MockData.put(a, b, c)
            }
        }
    }
}

@Composable
private fun StuffListItem(
    translation: TranslationDomain,
    modifier: Modifier = Modifier,
    onSaveClick: (key: String, loc: CombinedLocale, value: String) -> Unit,
) {
    val innerList by rememberUpdatedState(translation.values.toList())
    var isExpanded by rememberSaveable { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = translation.key
            )

            Text(
                text = translation.desc ?: "No description",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, true)
            )

            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                },
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier
                .align(Alignment.End)
        ) {
            Column {
                innerList.forEach { stuff ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        var text by remember(stuff.value){ mutableStateOf(stuff.value) }
                        Text(
                            text =stuff.locale.value
                        )
                        BasicTextField(
                            value = text,
                            onValueChange = {
                                text = it
                            }
                        )
                        if(text != stuff.value) {
                            Button(
                                onClick = {
                                    onSaveClick(stuff.parentKey, stuff.locale, text)
                                }
                            ) {
                                Text(
                                    text = "Save"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
