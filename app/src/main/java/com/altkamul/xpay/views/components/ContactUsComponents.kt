package com.altkamul.xpay.views.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.altkamul.xpay.ui.theme.Dimension
import com.altkamul.xpay.ui.theme.red

@Composable
fun TiltedEmailPhone(email: String, phone: String, text: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimension.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.secondaryVariant
        )
        /*** Row One */
        IconWithText(
            text = email,
            icon = Icons.Default.MailOutline,
            colors = MaterialTheme.colors.primary
        )

        /*** Row Tow */
        IconWithText(text = phone, icon = Icons.Default.PhoneInTalk, colors = red)
    }

}

@Composable
fun IconWithText(text: String, icon: ImageVector, colors: Color) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = Dimension.xs),
        horizontalArrangement = Arrangement.spacedBy(Dimension.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Contact Us Icon",
            tint = colors,
            modifier = Modifier.size(Dimension.mdIconSize)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.secondaryVariant
        )

    }
}
