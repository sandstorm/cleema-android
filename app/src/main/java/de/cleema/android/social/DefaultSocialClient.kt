package de.cleema.android.social

import android.content.Context
import android.content.Intent
import de.cleema.android.R
import javax.inject.Inject

class DefaultSocialClient @Inject constructor(
    private val context: Context,
    private val baseUri: String
) : SocialClient {
    override suspend fun invite(referralCode: String): Result<Unit> {
        val sharedText = context.getString(R.string.invite_user_template)
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            val link = "${baseUri}/invites/${referralCode}"
            putExtra(Intent.EXTRA_TEXT, sharedText + "\n" + link)
            putExtra(Intent.EXTRA_SUBJECT, sharedText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, sharedText).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return Result.runCatching {
            context.startActivity(shareIntent)
        }
    }
}
