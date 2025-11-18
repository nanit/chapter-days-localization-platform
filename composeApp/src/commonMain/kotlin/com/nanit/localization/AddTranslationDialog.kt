package com.nanit.localization

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun AddTranslationDialogPreview() {
    MaterialTheme {
        AddTranslationDialog(
            onDismiss = {},
            onApply = { _, _, _, _, _ -> }
        )
    }
}

@Composable
fun AddTranslationDialog(
    onDismiss: () -> Unit = {},
    onApply: (key: String, description: String?, enValue: String, esValue: String?, frValue: String?) -> Unit = { _, _, _, _, _ -> }
) {
    var key by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var enValue by remember { mutableStateOf("") }
    var esValue by remember { mutableStateOf("") }
    var frValue by remember { mutableStateOf("") }

    var showEsField by remember { mutableStateOf(false) }
    var showFrField by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add New Translation")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Key input field
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Key") },
                    placeholder = { Text("e.g., welcome_message") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Description input field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Brief description of this translation") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // English translation (always visible)
                Text(
                    text = "Translations",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = enValue,
                    onValueChange = { enValue = it },
                    label = { Text("English (EN)") },
                    placeholder = { Text("Enter English translation") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                // Spanish translation (optional)
                if (showEsField) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        OutlinedTextField(
                            value = esValue,
                            onValueChange = { esValue = it },
                            label = { Text("Spanish (ES)") },
                            placeholder = { Text("Enter Spanish translation") },
                            modifier = Modifier.weight(1f),
                            maxLines = 2
                        )
                        IconButton(
                            onClick = {
                                showEsField = false
                                esValue = ""
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Spanish translation"
                            )
                        }
                    }
                }

                // French translation (optional)
                if (showFrField) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        OutlinedTextField(
                            value = frValue,
                            onValueChange = { frValue = it },
                            label = { Text("French (FR)") },
                            placeholder = { Text("Enter French translation") },
                            modifier = Modifier.weight(1f),
                            maxLines = 2
                        )
                        IconButton(
                            onClick = {
                                showFrField = false
                                frValue = ""
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove French translation"
                            )
                        }
                    }
                }

                // Add more button (hidden when all 3 locales are shown)
                if (!showEsField || !showFrField) {
                    OutlinedButton(
                        onClick = {
                            when {
                                !showEsField -> showEsField = true
                                !showFrField -> showFrField = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Add More Translation")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onApply(
                        key,
                        description.takeIf { it.isNotEmpty() },
                        enValue,
                        if (showEsField) esValue.ifBlank { null } else null,
                        if (showFrField) frValue.ifBlank { null } else null
                    )
                },
                enabled = key.isNotBlank() && enValue.isNotBlank()
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
