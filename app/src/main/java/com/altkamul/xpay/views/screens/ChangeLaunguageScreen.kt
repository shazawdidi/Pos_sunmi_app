package com.altkamul.xpay.views.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.Language
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.XPayAndroidTheme
import com.altkamul.xpay.ui.theme.lightGray
import com.altkamul.xpay.viewmodel.ChangeLanguageViewModel
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChangeLanguageScreen(
    languageViewModel: ChangeLanguageViewModel = hiltViewModel()
) {
    XPayAndroidTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(Dimension.pagePadding),
        ) {
            /** All languages supported by our app */
            val languages = mutableListOf(
                Language.Arabic,
                Language.English,
                Language.French,
                Language.Urdu,
                Language.Turkish,
            )

            val currentLanguage = languageViewModel.currentLanguage.collectAsState("en")
            Timber.d("current language is $currentLanguage")
            /** Page title */
            Text(
                modifier = Modifier.fillMaxWidth(0.8f),
                text = stringResource(id = R.string.select_language),
                style = MaterialTheme.typography.h2.copy(color = MaterialTheme.colors.secondaryVariant)
            )
            Spacer(modifier = Modifier.height(Dimension.smLineMargin))
            /** Title's slug */
            Text(
                text = stringResource(R.string.language_slug),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondaryVariant.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(Dimension.sm))
            /** Our languages list */
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = Dimension.md),
                state = LazyListState(
                    languages.indexOf(languages.find { it.code == currentLanguage.value })
                )
            ){
                /** Iterating through our languages */
                items(languages){ language ->
                    LanguageItem(
                        language = language,
                        current = language.code == currentLanguage.value,
                        onLanguageSelected = {
                            /** Handling the event of choosing a new language , we should store its code of course */
                            languageViewModel.updateCurrentLanguage(newLanguage = language.code)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Single languages list's item , contain its icon, title, and a check icon if its the current language
 * It pass the click event up in case that clicked language is not already the current one .
 */
@Composable
fun LanguageItem(language: Language, current: Boolean, onLanguageSelected: () -> Unit) {
    Row(modifier = Modifier
        .padding(top = Dimension.sm)
        .fillMaxWidth()
        .clip(MaterialTheme.shapes.small)
        .background(MaterialTheme.colors.onSecondary)
        .border(
            width = 2.dp,
            shape = MaterialTheme.shapes.small,
            color = if (current) MaterialTheme.colors.secondary else Color.Transparent
        )
        .clickable {
            /** If the clicked language is not the current language , pass this event up */
            if (!current) onLanguageSelected()
        }
        .padding(Dimension.sm),verticalAlignment = Alignment.CenterVertically
    ){
        /** The flag */
        Image(
            modifier = Modifier.clip(MaterialTheme.shapes.small),
            painter = painterResource(id = language.icon),
            contentDescription = stringResource(id = language.title)
        )
        Spacer(modifier = Modifier.width(Dimension.sm))
        /** Language title */
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource( id = language.title),
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.secondaryVariant),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.width(Dimension.md))
        /** If its the current language , show a check mark icon at the end */
        if(current)
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = stringResource(id = language.title),
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier.size(Dimension.mdIconSize)
            )
    }
}
