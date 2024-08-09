/*
 * Created by Kumpels and Friends on 2023-01-23
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.cleema.android.R
import de.cleema.android.core.common.formatted
import de.cleema.android.core.styling.Action
import de.cleema.android.core.styling.DefaultText
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.FormatStyle

@Composable
fun ProfileUserView(
    user: de.cleema.android.core.models.User,
    modifier: Modifier = Modifier,
    onInviteClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingsClick: () -> Unit,
    onConvertClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(22.dp), modifier = modifier) {
        UserAvatar(user)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(user.name, style = MaterialTheme.typography.titleMedium)
            Column {
                Text(user.region.name, style = MaterialTheme.typography.bodyMedium)
                if (user.kind is de.cleema.android.core.models.User.Kind.Remote) {
                    Text(
                        stringResource(
                            id = R.string.profile_user_join_date,
                            user.joinDate.toLocalDateTime(TimeZone.UTC)
                                .formatted(FormatStyle.MEDIUM)
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    user.kind.title(),
                    style = MaterialTheme.typography.bodyMedium
                )

                de.cleema.android.core.components.InfoButton(
                    modifier = Modifier.alignBy(FirstBaseline),
                    icon = {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = DefaultText)
                    }
                ) {
                    Text(
                        user.kind.hint(),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            if (user.kind is de.cleema.android.core.models.User.Kind.Remote) {
                Divider(modifier = Modifier.padding(vertical = 8.dp), color = DefaultText)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SocialGraphButton(
                        user.followerCount,
                        stringResource(R.string.profile_user_followers),
                        onClick = onFollowersClick
                    )

                    SocialGraphButton(
                        user.followingCount,
                        stringResource(R.string.profile_user_following),
                        onClick = onFollowingsClick
                    )
                }
            }

            when (user.kind) {
                de.cleema.android.core.models.User.Kind.Local -> {
                    Button(
                        onClick = onConvertClick,
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Action
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.profile_convert_user), color = Color.White)
                    }

                    Text(
                        stringResource(R.string.profile_local_account_registration_hint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is de.cleema.android.core.models.User.Kind.Remote -> Button(
                    onClick = onInviteClick,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Action
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.profile_invite_user), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun de.cleema.android.core.models.User.Kind.title(): String {
    return when (this) {
        is de.cleema.android.core.models.User.Kind.Local -> stringResource(R.string.profile_user_account_type_local)
        is de.cleema.android.core.models.User.Kind.Remote -> stringResource(
            R.string.profile_user_account_type_server,
            email
        )
    }
}

@Composable
fun de.cleema.android.core.models.User.Kind.hint(): String {
    return when (this) {
        is de.cleema.android.core.models.User.Kind.Local -> stringResource(R.string.account_info_local)
        is de.cleema.android.core.models.User.Kind.Remote -> stringResource(R.string.account_info_remote)
    }
}
