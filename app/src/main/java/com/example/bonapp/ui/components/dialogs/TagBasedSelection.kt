package com.example.bonapp.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bonapp.ui.components.buttons.SelectableTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagBasedSelection(
    title: String,
    tags: Map<String, List<String>>,
    selectedTags: List<String>,
    onTagSelected: (String) -> Unit,
    onTagDeselected: (String) -> Unit
) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        tags.forEach { (category, categoryTags) ->
            Text(category, style = MaterialTheme.typography.titleMedium)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp), // Add horizontal spacing
                verticalArrangement = Arrangement.spacedBy(8.dp) // Add vertical spacing
            ) {
                categoryTags.forEach { tag ->
                    SelectableTag(
                        name = tag,
                        isSelected = tag in selectedTags,
                        onTagClick = {
                            if (tag in selectedTags) {
                                onTagDeselected(tag)
                            } else {
                                onTagSelected(tag)
                            }
                        },
                        modifier = Modifier.padding(bottom = 8.dp), // Add vertical spacing
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}