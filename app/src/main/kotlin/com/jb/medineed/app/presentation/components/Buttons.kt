package com.jb.medineed.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import com.jb.medineed.app.R
import com.jb.medineed.app.presentation.common.HapticFeedback.slightHapticFeedback

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BackButton(onClick: () -> Unit) {
    val view = LocalView.current
    IconButton(
        modifier = Modifier,
        shapes = IconButtonDefaults.shapes(),
        onClick = {
            onClick()
            view.slightHapticFeedback()
        },
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}