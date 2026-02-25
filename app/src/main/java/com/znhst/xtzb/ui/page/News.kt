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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    var currentPlate by rememberSaveable { mutableStateOf("common") } //common | local

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
    var expanded by rememberSaveable { mutableStateOf(false) }
    var campusList by rememberSaveable {
        mutableStateOf(
            listOf<String>(
                "园区一",
                "园区二"
            )
        )
    }
    var searchQuery by rememberSaveable { mutableStateOf(campusList[0]) }
    val focusRequester = remember { FocusRequester() } // 初始化 FocusRequester

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { onClickCommon() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.alpha(alpha = if (currentPlate == "common") 1f else 0.6f)
                ) {
                    Text(
                        "公共",
                        fontSize = 16.sp,
                        color = if (currentPlate == "common") Color.White else Color.White,
                        fontWeight = if (currentPlate == "common") FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onClickLocal() },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.alpha(alpha = if (currentPlate == "local") 1f else 0.6f)
                ) {
                    Text(
                        "本地",
                        fontSize = 16.sp,
                        color = if (currentPlate == "local") Color.White else Color.White,
                        fontWeight = if (currentPlate == "local") FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(Modifier.width(8.dp))

                // 可搜索下拉选择框
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
                            // 调用接口更新园区列表
                            // Example: viewModel.fetchCampusList(query)
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .focusRequester(focusRequester) // 绑定 FocusRequester
                            .onFocusChanged { focusState ->
                                if (!focusState.isFocused) expanded = false
                            },
                        singleLine = true
                    ) { innerTextField ->
                        OutlinedTextFieldDefaults.DecorationBox(
                            value = searchQuery,
                            placeholder = {
                                Text("选择园区", fontSize = 16.sp)
                            },
                            innerTextField = innerTextField,
                            enabled = true,
                            singleLine = true,
                            visualTransformation = VisualTransformation.None,
                            interactionSource = remember { MutableInteractionSource() },
                            suffix = {
                                Icon(Icons.Filled.KeyboardArrowDown, "选择园区", tint = Color.DarkGray)
                            },
                            contentPadding = PaddingValues(
                                top = 0.dp,
                                bottom = 0.dp,
                                start = 8.dp,
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
                .align(Alignment.BottomCenter)
        )
    }
}