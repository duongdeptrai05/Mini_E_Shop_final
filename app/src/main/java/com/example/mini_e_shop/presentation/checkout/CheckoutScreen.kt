package com.example.mini_e_shop.presentation.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    // EDIT: Remove CartViewModel
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    // EDIT: Add a lambda to show snackbar from MainActivity
    onShowSnackbar: (String) -> Unit
) {
    val uiState by checkoutViewModel.uiState.collectAsState()

    // EDIT: Listen for events from CheckoutViewModel
    LaunchedEffect(key1 = true) {
        checkoutViewModel.eventFlow.collect { event ->
            when(event) {
                is CheckoutEvent.NavigateBack -> onNavigateBack()
                is CheckoutEvent.ShowSnackbar -> onShowSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Order") },
                // Add a back button
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (uiState is CheckoutUiState.Success) {
                val successState = uiState as CheckoutUiState.Success
                Button(
                    // EDIT: Call the placeOrder function on the correct ViewModel
                    onClick = { checkoutViewModel.placeOrder() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                ) {
                    Text("Confirm Order - $${String.format("%.2f", successState.totalPrice)}", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is CheckoutUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CheckoutUiState.Success -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        item {
                            Text("Items in your order:", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        items(state.items) { cartItemDetail ->
                            // TODO: Create a nicer Composable to display the item
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${cartItemDetail.product.name} (x${cartItemDetail.cartItem.quantity})")
                                Text("$${cartItemDetail.product.price * cartItemDetail.cartItem.quantity}")
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
                is CheckoutUiState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

