import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelectionDialog(
    title: String,
    tags: Map<String, List<String>>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
    onTagDeselected: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var tempSelectedTags by remember { mutableStateOf(selectedTags) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
            ) {
                tags.forEach { (category, categoryTags) ->
                    item {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    item {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoryTags.forEach { tag ->
                                AssistChip(
                                    onClick = {
                                        tempSelectedTags = if (tag in tempSelectedTags) {
                                            tempSelectedTags - tag
                                        } else {
                                            tempSelectedTags + tag
                                        }
                                    },
                                    label = { Text(tag) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = if (tag in tempSelectedTags)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                tempSelectedTags.forEach { tag ->
                    if (tag !in selectedTags) {
                        onTagSelected(tag)
                    }
                }
                selectedTags.forEach { tag ->
                    if (tag !in tempSelectedTags) {
                        onTagDeselected(tag)
                    }
                }
                onConfirm()
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}