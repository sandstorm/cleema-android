/*
 * Created by Kumpels and Friends on 2023-01-27
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

enum class PartnerType {
    STARTER,
    DARLINGS,
    LEARNING
}

data class PartnerPackage(
    val type: PartnerType = PartnerType.STARTER,
    val title: String = "",
    val copy: String = "",
    val features: String,
    val image: RemoteImage? = null
) {
    companion object {
        val starter = PartnerPackage(
            PartnerType.STARTER,
            "Starter",
            "Werbekunden",
            "Wissen vermitteln, Werte transportieren, Menschen aktivieren. Glaubhafte Werbung bringt euch eurer Zielgruppe näher - und gelingt mit wenig Aufwand. Mit cleemaStarter bucht ihr Werbeleistungen im Rahmen unserer App. Eine einfache Schritt-für-Schritt-Anleitung ermöglicht euch das Sponsoring einer Challenge, die Durchführung einer Kurzbefragung oder den Start eures eigenen Projekts."
        )
        val darlings = PartnerPackage(
            PartnerType.DARLINGS,
            "Lieblinge",
            "Marktplatz",
            "Auf unserem Marktplatz werdet ihr für cleema-Nutzer:innen sichtbar. Ob Restaurant, Modeboutique oder Gesundheitsdienstleistungen - cleemaLieblinge ist eure Möglichkeit, lokale Kund:innen über aktuelle (Einkaufs-)Aktionen zu informieren, die Reichweite eurer Angebote zu vergrößern und den Absatz eurer Produkte zu steigern. Gewinnt eure Stammkundschaft von morgen."
        )
        val partner = PartnerPackage(
            PartnerType.LEARNING,
            "Lernen",
            "Unternehmen",
            "Erfolgreiche Unternehmen sind nachhaltig. Mit cleemaLernen erhaltet ihr ein intuitives Einstiegstool, das euch dabei hilft, eure Klimabilanz zu verbessern, Mitarbeitende im Prozess mitzunehmen und wirksame wie anschauliche Beispiele für euren Nachhaltigkeitsbericht aufzubauen. Mit cleemaLernen wird Zukunft für alle Beteiligten greifbar."
        )
        val all = listOf(starter, darlings, partner)
    }
}
