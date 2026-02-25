package com.znhst.xtzb.ui.page

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun News(outNavController: NavController) {
    val navController = rememberNavController()
    var currentPlate by remember { mutableStateOf("common") }

    fun onClickCommon() {
        currentPlate = "common"
        navController.navigate("common_news")
    }

    fun onClickLocal() {
        currentPlate = "local"
        navController.navigate("local_news")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TopStickyHeader(
            currentPlate,
            onClickCommon = { onClickCommon() },
            onClickLocal = { onClickLocal() },
            onCampusSelected = {})

        Spacer(modifier = Modifier.height(56.dp))

        NavHost(
            navController = navController,
            startDestination = "common_news",
            modifier = Modifier.padding(top = 56.dp)
        ) {
            composable("common_news") {
                CommonNews(navController = navController, outNavController = outNavController)
            }
            composable("local_news") {
                LocalNews(navController = navController, outNavController = outNavController)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopStickyHeader(
    currentPlate: String,
    onClickCommon: () -> Unit,
    onClickLocal: () -> Unit,
    onCampusSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var campusList by remember {
        mutableStateOf(
            listOf<String>(
                "园区一",
                "园区二"
            )
        )
    }
    var searchQuery by remember { mutableStateOf(campusList[0]) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = currentPlate == "common",
                    onClick = { onClickCommon() },
                    label = {
                        Text(
                            "公共",
                            fontSize = 14.sp,
                            fontWeight = if (currentPlate == "common") FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = currentPlate == "local",
                    onClick = { onClickLocal() },
                    label = {
                        Text(
                            "本地",
                            fontSize = 14.sp,
                            fontWeight = if (currentPlate == "local") FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(Modifier.width(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicTextField(
                        readOnly = true,
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) expanded = false
                            },
                        singleLine = true
                    ) { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = searchQuery,
                            placeholder = {
                                Text("选择园区", fontSize = 14.sp)
                            },
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            suffix = {
                                Icon(Icons.Filled.KeyboardArrowDown, "选择园区", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            contentPadding = PaddingValues(
                                top = 0.dp,
                                bottom = 0.dp,
                                start = 12.dp,
                                end = 8.dp
                            )
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        campusList.forEach { campus ->
                            TextButton(
                                onClick = {
                                    expanded = false
                                    onCampusSelected(campus)
                                    searchQuery = campus
                                }
                            ) {
                                Text(text = campus, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
