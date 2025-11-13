package com.example.weather.weather.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontStyle
import com.example.weather.weather.WeatherState

/**
 * A Composable that provides a text field for city input.
 *
 * @param weatherState The current state of the weather screen, used to control the enabled state and error display.
 * @param city The current text in the input field.
 * @param focusRequester A [FocusRequester] to control the focus of the text field.
 * @param onValueChange A callback that is triggered when the text in the input field changes.
 * @param onEnter A callback that is triggered when the Enter key is pressed.
 */
@Composable
fun CityInput(
    weatherState: WeatherState,
    city: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onEnter: () -> Unit,
) {
    TextField(
        value = city,
        onValueChange = onValueChange,
        enabled = weatherState !is WeatherState.Loading,
        singleLine = true,
        isError = weatherState is WeatherState.ValidationError,
        supportingText = {
            if (weatherState is WeatherState.ValidationError) {
                Text("Please enter a valid city name.")
            }
        },
        modifier = Modifier.focusRequester(focusRequester).onKeyEvent {
            if (it.key == Key.Enter) {
                onEnter()
                true
            } else {
                false
            }
        },
        placeholder = {
            Text("Enter a city name...", fontStyle = FontStyle.Italic)
        },
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Cloud, contentDescription = "Cloud")
        },
    )
}
