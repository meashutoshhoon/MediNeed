package com.jb.medineed.app.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.jb.medineed.app.util.PreferenceUtil.getBoolean
import com.jb.medineed.app.util.PreferenceUtil.getInt

inline val String.booleanState
    @Composable get() = remember { mutableStateOf(this.getBoolean()) }

inline val String.intState
    @Composable get() = remember { mutableIntStateOf(this.getInt()) }