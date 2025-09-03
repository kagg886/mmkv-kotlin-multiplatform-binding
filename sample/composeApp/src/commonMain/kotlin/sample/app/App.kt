package sample.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import top.kagg886.mkmb.MMKV
import top.kagg886.mkmb.defaultMMKV
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun App() {
    val data = remember {
        mutableStateMapOf<String, Any>()
    }

    val mmkv = remember {
        ListenableMMKV(MMKV.defaultMMKV()) { key, value ->
            if (value == null) {
                data.remove(key)
            } else {
                data[key] = value
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            AppFab(
                mmkv = mmkv
            )
        }
    ) {
        AppContent(
            modifier = Modifier.padding(it),
            data = data,
            mmkv = mmkv
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppFab(
    modifier: Modifier = Modifier,
    mmkv: MMKV,
) {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Text("+")
    }

    if (showDialog) {
        AddKeyValueDialog(
            onDismiss = { showDialog = false },
            onAdd = { key, value, type ->
                when (type) {
                    "Int" -> mmkv.set(key, value.toInt())
                    "Long" -> mmkv.set(key, value.toLong())
                    "Float" -> mmkv.set(key, value.toFloat())
                    "Double" -> mmkv.set(key, value.toDouble())
                    "String" -> mmkv.set(key, value)
                    "Boolean" -> mmkv.set(key, value.toBoolean())
                    "List<String>" -> {
                        val list = value.split(",").map { it.trim() }
                        mmkv.set(key, list)
                    }
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun AppContent(
    modifier: Modifier = Modifier,
    data: Map<String, Any>,
    mmkv: MMKV
) {
    var selectedKey by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Text(
            text = "MMKV Key-Value Pairs",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (data.isEmpty()) {
            Text(
                text = "No data available. Click the + button to add key-value pairs.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(data.entries.toList()) { (key, value) ->
                    ListItem(
                        headlineContent = { Text("Key: $key") },
                        supportingContent = { Text("Value: ${formatValue(value)}") },
                        trailingContent = { Text("Type: ${getDisplayType(value)}") },
                        modifier = modifier.clickable { selectedKey = key }
                    )
                }
            }
        }
    }

    selectedKey?.let { key ->
        EditKeyValueDialog(
            key = key,
            value = data[key]!!,
            onDismiss = { selectedKey = null },
            onDelete = {
                mmkv.remove(key)
                selectedKey = null
            },
            onUpdate = { newValue, type ->
                when (type) {
                    "Int" -> mmkv.set(key, newValue.toInt())
                    "Long" -> mmkv.set(key, newValue.toLong())
                    "Float" -> mmkv.set(key, newValue.toFloat())
                    "Double" -> mmkv.set(key, newValue.toDouble())
                    "String" -> mmkv.set(key, newValue)
                    "Boolean" -> mmkv.set(key, newValue.toBoolean())
                    "List<String>" -> {
                        val list = newValue.split(",").map { it.trim() }
                        mmkv.set(key, list)
                    }
                }
                selectedKey = null
            }
        )
    }
}

class ListenableMMKV(private val mmkv: MMKV,private val notify: (String,Any?) -> Unit): MMKV by mmkv {
    override fun set(key: String, value: Int, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }

    override fun set(key: String, value: String, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }
    override fun set(key: String, value: ByteArray, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }
    override fun set(key: String, value: List<String>, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }

    override fun set(key: String, value: Boolean, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }
    override fun set(key: String, value: Long, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }
    override fun set(key: String, value: Float, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }

    override fun set(key: String, value: Double, expire: Int) {
        notify(key,value)
        mmkv.set(key,value,expire)
    }


    override fun remove(key: String): Boolean {
        notify(key,null)
        return mmkv.remove(key)
    }

    override fun clear() {
        for (i in allKeys()) {
            notify(i,null)
        }

        mmkv.clear()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddKeyValueDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("String") }
    var expanded by remember { mutableStateOf(false) }

    val types = listOf("Int", "Long", "Float", "Double", "String", "Boolean", "List<String>")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Key-Value Pair") },
        text = {
            Column {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Key") },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(getValueLabel(selectedType)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (selectedType) {
                            "Int", "Long" -> KeyboardType.Number
                            "Float", "Double" -> KeyboardType.Decimal
                            else -> KeyboardType.Text
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (key.isNotBlank() && value.isNotBlank()) {
                        try {
                            onAdd(key, value, selectedType)
                        } catch (e: Exception) {
                            // Can add error handling
                        }
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EditKeyValueDialog(
    key: String,
    value: Any,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String, String) -> Unit
) {
    var newValue by remember { mutableStateOf(formatEditableValue(value)) }
    val type = getDisplayType(value)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Key-Value Pair") },
        text = {
            Column {
                Text(
                    text = "Key: $key",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Type: ${getDisplayType(value)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = newValue,
                    onValueChange = { newValue = it },
                    label = { Text(getValueLabel(type)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (type) {
                            "Int", "Long" -> KeyboardType.Number
                            "Float", "Double" -> KeyboardType.Decimal
                            else -> KeyboardType.Text
                        }
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newValue.isNotBlank()) {
                        try {
                            onUpdate(newValue, type)
                        } catch (e: Exception) {
                            // Can add error handling
                        }
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            // Delete button
            TextButton(
                onClick = onDelete,
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

private fun formatValue(value: Any): String {
    return when (value) {
        is List<*> -> value.joinToString(", ", "[", "]")
        else -> value.toString()
    }
}

private fun formatEditableValue(value: Any): String {
    return when (value) {
        is List<*> -> value.joinToString(", ")
        else -> value.toString()
    }
}

private fun getValueLabel(type: String): String {
    return when (type) {
        "List<String>" -> "Value (comma separated)"
        "Boolean" -> "Value (true/false)"
        else -> "Value"
    }
}

private fun getDisplayType(value: Any): String {
    return when (value) {
        is List<*> -> "List<String>"
        is Int -> "Int"
        is Long -> "Long"
        is Float -> "Float"
        is Double -> "Double"
        is String -> "String"
        is Boolean -> "Boolean"
        else -> value::class.simpleName ?: "Unknown"
    }
}
