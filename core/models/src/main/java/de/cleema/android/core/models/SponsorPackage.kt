/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

enum class SponsorType {
    FAN,
    MAKER,
    LOVE
}

data class SponsorPackage(
    val type: SponsorType = SponsorType.FAN,
    val title: String = "",
    val description: String = "",
    val markdown: String = "",
    val price: Int = 0,
    val image: RemoteImage? = null
) {

    companion object {
        val fan = SponsorPackage(
            SponsorType.FAN,
            "Fan",
            "Für alle, die\netwas bewegen wollen",
            "In unserem jährlichen cleema Report zeigen wir dir, wie sich unser Startup entwickelt und was hinter den Kulissen geschieht. Du erfährst, wofür wir deinen monatlichen Beitrag verwenden und was wir damit bewirken. Wir berichten über unsere nächsten Ziele, die wir gemeinsam mit der cleema Community erreichen wollen. Unser ausführlicher Jahresbericht erreicht dich im PDF-Format.\n\n#### Werde als cleemaFan sichtbar\nIn deinem Profil wirst du als Fördermitglied sichtbar. So zeigst du dein Engagement in der Community.",
            5
        )
        val maker = SponsorPackage(
            SponsorType.MAKER,
            "Macher",
            "Für alle, die\nenergiegeladen sind",
            "Als cleema Macher kannst du an Umfragen und Votings teilnehmen und damit die Weiterentwicklung unserer cleema App mitbestimmen. Unabhängig von deiner Teilnahme erhältst du die Umfrageergebnisse und erfährst, welche Entscheidungen auf dieser Basis getroffen werden.\n\n#### Sichere dir exklusive Informationen\nIn unserem jährlichen cleema Report zeigen wir dir, wie sich unser Startup entwickelt und was hinter den Kulissen geschieht. Du erfährst, wofür wir deinen monatlichen Beitrag verwenden und was wir damit bewirken. Wir berichten über unsere nächsten Ziele, die wir gemeinsam mit der cleema Community erreichen wollen. Unser ausführlicher Jahresbericht erreicht dich im PDF-Format.\n\n#### Werde als cleemaFan sichtbar\nIn deinem Profil wirst du als Fördermitglied sichtbar. So zeigst du dein Engagement in der Community.",
            10
        )
        val love = SponsorPackage(
            SponsorType.LOVE,
            "Liebe",
            "Für alle, die\netwas mehr wollen.",
            "Unser Liebesgeschenk an dich ist eine Überraschungskiste, die wir dir jährlich zusenden. Freue dich auf nachhaltig produzierte Produkte.\n\nDu kannst du an Umfragen und Votings teilnehmen und damit die Weiterentwicklung unserer cleema App mitbestimmen. Unabhängig von deiner Teilnahme erhältst du die Umfrageergebnisse und erfährst, welche Entscheidungen auf dieser Basis getroffen werden.\n\n#### Bestimme mit\nDu kannst du an Umfragen und Votings teilnehmen und damit die Weiterentwicklung unserer cleema App mitbestimmen. Unabhängig von deiner Teilnahme erhältst du die Umfrageergebnisse und erfährst, welche Entscheidungen auf dieser Basis getroffen werden.\n\n#### Sichere dir exklusive Informationen\nIn unserem jährlichen cleema Report zeigen wir dir, wie sich unser Startup entwickelt und was hinter den Kulissen geschieht. Du erfährst, wofür wir deinen monatlichen Beitrag verwenden und was wir damit bewirken. Wir berichten über unsere nächsten Ziele, die wir gemeinsam mit der cleema Community erreichen wollen. Unser ausführlicher Jahresbericht erreicht dich im PDF-Format.\n\n#### Werde als cleemaFan sichtbar\nIn deinem Profil wirst du als Fördermitglied sichtbar. So zeigst du dein Engagement in der Community.",
            25
        )
        val all = listOf(fan, maker, love)
    }
}
