package de.cleema.android.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import de.cleema.android.R
import de.cleema.android.core.styling.Accent
import de.cleema.android.core.styling.DefaultText
import de.cleema.android.core.styling.Dimmed

@Composable
fun PendingConfirmationScreen(
    mail: String,
    modifier: Modifier = Modifier,
    onResetTapped: () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = false)
        systemUiController.setNavigationBarColor(Dimmed)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Accent)
            .systemBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(stringResource(R.string.newuser_welcome))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .height(63.dp),
                colorFilter = ColorFilter.tint(DefaultText)
            )

            Text(text = stringResource(R.string.newuser_hint), fontWeight = FontWeight.Bold)

            Text(
                text = stringResource(R.string.newuser_description),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )

            AccountConfirmation(mail, onResetTapped = onResetTapped)

            Spacer(modifier = Modifier.weight(1f))


        }
        Image(
            painter = painterResource(R.drawable.login_wave),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(0.dp)
                .fillMaxWidth(),
            alignment = Alignment.BottomCenter
        )
    }
}

