package com.kontranik.easycycle.ui.appnavigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.easycycle.ui.DrawerParams.drawerButtons
import com.kontranik.easycycle.ui.Screen
import com.kontranik.easycycle.ui.appdrawer.AppDrawerItemInfo
import com.kontranik.easycycle.ui.theme.EasyCycleTheme

@Composable
fun AppNavigationScaffold(
    menuItems: List<AppDrawerItemInfo>,
    defaultPick: Screen,
    showLabel: Boolean,
    onClick: (Screen) -> Unit,
    content:  @Composable (() -> Unit) = {}
) {
    var currentPick by rememberSaveable() { mutableStateOf(defaultPick.route) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            menuItems.forEach {
                item(
                    icon = {
                        it.imageVector?.let { imageVector ->
                            Icon(
                                imageVector = imageVector,
                                contentDescription = stringResource(it.descriptionId)
                            )
                        }
                        it.drawableId?.let { drawableId ->
                            Icon(
                                painter = painterResource(drawableId),
                                contentDescription = stringResource(it.descriptionId)
                            )
                        }
                    },
                    selected = it.drawerOption.route == currentPick,
                    onClick = {
                        currentPick = it.drawerOption.route;
                        onClick(it.drawerOption)
                    },
                    label = {
                        if (showLabel || it.drawerOption.route == currentPick) Text(stringResource(it.descriptionId))
                    }
                )
            }
        },
        content = content
    )
}

@Preview
@Composable
private fun AppDrawerContentPreview() {
    EasyCycleTheme {
        AppNavigationScaffold(
            menuItems = drawerButtons,
            defaultPick = Screen.Calendar,
            showLabel = false,
            onClick = {}
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Text(
                    text = "Hello there!",
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}