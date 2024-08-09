package de.cleema.android.core.models

data class SocialGraph(val followers: List<SocialGraphItem> = listOf(), val following: List<SocialGraphItem> = listOf())
