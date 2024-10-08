package com.znhst.xtzb.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.znhst.xtzb.viewModel.AuthViewModel

@Composable
fun Profile(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            logout(viewModel, navController)
        }) {
            Text("注销")
        }
    }
}