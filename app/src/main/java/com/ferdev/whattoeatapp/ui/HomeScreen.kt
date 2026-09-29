package com.ferdev.whattoeatapp.ui

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferdev.whattoeatapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGoToRegister: () -> Unit,
    onGoToSearch: () -> Unit
) {
    val context = LocalContext.current
    var showLangMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = { showLangMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(R.string.home_language)
                        )
                    }
                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.common_language_english)) },
                            onClick = {
                                changeLanguage(context, "en")
                                showLangMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.common_language_spanish)) },
                            onClick = {
                                changeLanguage(context, "es")
                                showLangMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🍽️ " + stringResource(R.string.home_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.home_subtitle),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Button(
                onClick = onGoToSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.home_search_button), fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onGoToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.home_register_button), fontSize = 18.sp)
            }
        }
    }
}

private fun changeLanguage(context: Context, langCode: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("app_language", langCode).apply()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val localeManager = context.getSystemService(Context.LOCALE_SERVICE) as android.app.LocaleManager
        val localeList = android.os.LocaleList(java.util.Locale.forLanguageTag(langCode))
        localeManager.applicationLocales = localeList
    } else {
        val locale = java.util.Locale(langCode)
        java.util.Locale.setDefault(locale)
        val config = android.content.res.Configuration(context.resources.configuration)
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        (context as? android.app.Activity)?.recreate()
    }
}