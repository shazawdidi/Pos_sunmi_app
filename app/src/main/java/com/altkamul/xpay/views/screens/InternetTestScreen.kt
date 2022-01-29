package com.altkamul.xpay.views.screens

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.altkamul.xpay.R
import com.altkamul.xpay.sealed.NetworkStatus
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.viewmodel.InternetTestViewModel
import com.altkamul.xpay.viewmodel.ParentViewModel


@Composable
fun InternetTestScreen(
    internetTestViewModel: InternetTestViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(Dimension.pagePadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val context = LocalContext.current
        val parentViewModel: ParentViewModel = viewModel(context as ComponentActivity)
        val isConnected = parentViewModel.network.observeAsState().run {
            value == NetworkStatus.Connected
        }

        val speed by remember { internetTestViewModel.speed }
        val unit by remember { internetTestViewModel.unit }
        val progress by remember { internetTestViewModel.progress }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = TweenSpec(durationMillis = 1000)
        )
        var text = when{
            animatedProgress < 1.0f -> stringResource(R.string.checking_internet_running)
            else -> stringResource(R.string.internet_checked)
        }

        if(isConnected){
            LaunchedEffect(true){
                internetTestViewModel.checkInternetSpeed()
            }
        } else {
            text = stringResource(id = R.string.no_internet)
        }

        Box(
            modifier = Modifier
                .size(240.dp)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ){
            val progressTint = if(isConnected) MaterialTheme.colors.primary else Color.Red
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(180f)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.surface),
                progress = animatedProgress,
                color = progressTint,
                strokeWidth = Dimension.sm
            )
            if(animatedProgress == 1.0f){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "$speed", style = MaterialTheme.typography.h3)
                    Spacer(modifier = Modifier.height(Dimension.xs))
                    Text(
                        text = if (unit == "kb") stringResource(R.string.kb_per_second) else stringResource(R.string.mb_per_second, ),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.secondary,
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(Dimension.pagePadding))
        Text(text = text, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(Dimension.xs))
        Text(
            text = stringResource(R.string.recheck),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colors.secondary)
                .clickable {
                    internetTestViewModel.reCheckInternetSpeed()
                }
                .padding(horizontal = Dimension.pagePadding, vertical = Dimension.xs)
        )
    }
}