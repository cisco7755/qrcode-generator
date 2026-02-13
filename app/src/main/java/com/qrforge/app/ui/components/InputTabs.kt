package com.qrforge.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qrforge.app.data.QrInputType

@Composable
fun InputTabs(
    selected: QrInputType,
    onSelect: (QrInputType) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = QrInputType.entries
    TabRow(
        selectedTabIndex = tabs.indexOf(selected),
        modifier = modifier.fillMaxWidth()
    ) {
        tabs.forEach { type ->
            Tab(
                selected = selected == type,
                onClick = { onSelect(type) },
                text = { Text(type.label) }
            )
        }
    }
}
