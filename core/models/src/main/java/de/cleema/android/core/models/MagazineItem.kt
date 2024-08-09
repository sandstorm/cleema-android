/*
 * Created by Kumpels and Friends on 2022-11-18
 * Copyright © 2022 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.models

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

data class MagazineItem(
    val id: UUID = UUID.randomUUID(),
    val title: String = "",
    val description: String = "",
    val date: Instant = Clock.System.now(),
    val publishedAt: Instant = Clock.System.now(),
    val tags: List<Tag> = listOf(),
    val image: RemoteImage? = null,
    val teaser: String = "",
    val type: ItemType = ItemType.TIP,
    val region: Region? = null,
    val faved: Boolean = false,
) {
    enum class ItemType { NEWS, TIP }
    companion object {
        val demo = arrayListOf(
            MagazineItem(
                id = UUID.randomUUID(),
                "E-Books für Vielleser",
                description = "Wer gewinnt das Nachhaltigkeitsduell?\n" +
                        "\n" +
                        "Wer viel und gerne liest, hat sicher auch mal über die Nutzung eines E-Book Readers nachgedacht. Diese Geräte können sehr vieler Bücher speichern und sind daher insbesondere bei Reisen die komfortablere Wahl. Doch wie sieht die Umweltbilanz, verglichen mit gedruckten Büchern aus? \n" +
                        "\n" +
                        "Betrachtet man nur die Herstellung, ist die Produktion eines E-Book Readers für etwa 24 kg CO2 Äquivalente verantwortlich. Ein einziges Buch dagegen nur für etwa 1 kg. Theoretisch haben elektronische Bücher daher die Nase vorn, wenn mindestens 25 Stück davon auf einem Gerät gespeichert sind (und auch gelesen werden).\n" +
                        "\n" +
                        "Allerdings muss bei der Nutzung auch der Stromverbrauch des Gerätes mit berücksichtigt werden. Je nachdem, ob mit Öko- oder Graustrom geladen wird, entstehen weitere Emissionen. CO2 emitierts du aber auch, wenn du dir gedruckte Bücher nach Hause liefern lässt oder mit dem Auto zur Buchhandlung fährst.\n" +
                        "\n" +
                        "Welche Alternative letztlich klimafreundlicher ist, entscheidet also das persönliche Verhalten. Eine Studie aus dem Jahr 2011 gibt allerdings eine grobe Orientierung: Demzufolge sind E-Book Reader für Personen geeignet, die über mindestens 3 Jahre hinweg durchschnittlich 30 oder mehr Bücher lesen.\n" +
                        "\n" +
                        "Quelle: [https://www.quarks.de/umwelt/sind-e-book-reader-umweltfreundlicher-als-buecher/](https://www.quarks.de/umwelt/sind-e-book-reader-umweltfreundlicher-als-buecher/)",
                date = Clock.System.now(),
                publishedAt = Clock.System.now(),
                tags = arrayListOf(
                    Tag("Dresden"), Tag("Ernährung und Lebensmittel")
                ),
                image = RemoteImage.of(
                    url = "https://loremflickr.com/1035/624",
                    size = Size(345f, 208f)
                ),
                teaser = "Wer gewinnt das Nachhaltigkeitsduell?\n" +
                        "\n" +
                        "Wer viel und gerne liest, hat sicher auch mal über die Nutzung eines E-Book Readers nachgedacht.",
            ),
            MagazineItem(
                id = UUID.randomUUID(),
                "E-Scooter nützen dem Klima nicht",
                description = "Elektro-Roller erfreuen sich seit einigen Jahren großer Beliebtheit und gelten wegen ihres elektrischen Antriebs als umweltfreundliches Fortbewegungsmittel. Doch ist dieses Image überhaupt gerechtfertigt? \n" +
                        "\n" +
                        "Eine Studie der ETH Zürich kommt zu der Erkenntnis, dass die Nutzung der E-Scooter eher klimaschädlich ist. Das liegt daran, dass die Roller in der Regel nur sehr kurze Strecken ersetzen, die sonst zu Fuß, mit dem Fahrrad oder dem ÖPNV zurückgelegt worden wären.\n" +
                        "\n" +
                        "Einen echten, umweltfreundlichen Vorteil würden die Elektro-Roller nur dann leisten, wenn dafür auf das Auto verzichtet wird. Doch das ist in den meisten Großstädten leider nur selten der Fall, wie die Studie zeigt. Und selbst der Sharing-Ansatz bringt eher Nachteile, weil die Menschen leider nicht sehr sorgsam mit den Rollern umgehen. Manche landen gar in Parks oder Flüssen.\n" +
                        "\n" +
                        "Privat genutzte E-Scooter bleiben im Vergleich fast doppelt so lange im Einsatz, bis sie gewartet oder ersetzt werden müssen. Allen gemeinsam ist jedoch das nicht zu unterschätzende Unfallrisiko.\n" +
                        "\n" +
                        "Quelle: [https://www.nationalgeographic.de/umwelt/2022/01/warum-e-scooter-dem-klima-mehr-schaden-als-nuetzen](https://www.nationalgeographic.de/umwelt/2022/01/warum-e-scooter-dem-klima-mehr-schaden-als-nuetzen)",
                date = Clock.System.now(),
                publishedAt = Clock.System.now(),
                tags = arrayListOf(),
                image = RemoteImage.of(
                    url = "https://loremflickr.com/1035/624",
                    size = Size(345f, 208f)
                ),
                teaser = "Elektro-Roller erfreuen sich seit einigen Jahren großer Beliebtheit und gelten wegen ihres elektrischen Antriebs als umweltfreundliches Fortbewegungsmittel. Doch ist dieses Image überhaupt gerechtfertigt? \n" +
                        "\n" +
                        "Eine Studie der ETH Zürich kommt zu der Erkenntnis, dass die Nutzung der E-Scooter eher klimaschädlich ist.",
            ),
            MagazineItem(
                id = UUID.randomUUID(),
                "Fische sollten den Ozean nur unverpackt überqueren",
                description = "Schon gewusst? Um Kosten zu sparen wird der in Norwegen gefangene Kabeljau in China filetiert, um dann wieder zurück in die EU geliefert zu werden. Ergibt das für dich Sinn? Für uns nicht! Deshalb setzen wir lieber auf regionalen Fang, wenn wir Appetit auf Fisch haben. Hol dir eine leckere Forelle aus heimischen Bächen wie der Kirnitzsch oder einen Karpfen aus sächsischen Teichen, zum Beispiel aus Moritzburg.",
                date = Clock.System.now(),
                publishedAt = Clock.System.now(),
                tags = arrayListOf(
                    Tag("Dresden"), Tag("Ernährung und Lebensmittel")
                ),
                image = RemoteImage.of(
                    url = "https://loremflickr.com/1035/624",
                    size = Size(345f, 208f)
                ),
                teaser = "Schon gewusst? Um Kosten zu sparen wird der in Norwegen gefangene Kabeljau in China filetiert, um dann wieder zurück in die EU geliefert zu werden.",
            ),
            MagazineItem(
                id = UUID.randomUUID(),
                "Online Shopping? Aber nachhaltig!",
                description = "Du findest im Laden einfach nicht das, was du suchst? Online Shopping ist zwar grundsätzlich zweite Wahl, lässt sich aber leider nicht immer vermeiden. Hier ein paar Tipps, wie du auch beim Online-Shoppen CO2 sparen kannst:\n" +
                        "\n" +
                        "Vergewissere dich, dass du die Sache wirklich neu kaufen möchtest und nicht in deiner Umgebung leihen oder regional besorgen kannst. Nutze Online-Shops, die nachhaltige Produkte anbieten, die Ware klimaneutral versenden sowie soziale und ökologische Projekte unterstützen. Wenn der Anbieter auch eine Filiale in Dresden betreibt, kannst du die bestellte Ware dort abholen.\n" +
                        "\n" +
                        "- Bevorzuge regionale Shops und vermeide Bestellungen aus fernen Ländern, z.B. USA, Hongkong oder China. \n" +
                        "- Sammle mehrere Wünsche für eine Bestellung und bestelle auch mit anderen gemeinsam, um mehrere Lieferungen zu vermeiden. \n" +
                        "- Versuche, nur bei wenigen Online-Shops einzukaufen. \n" +
                        "- Vermeide mehrere Teillieferungen von einer Bestellung.\n" +
                        "- Verzichte auf Expressversand, damit die Zusteller Pakete sammeln und gemeinsam auszuliefern können. Wenn du das Paket nicht selbst annehmen kannst, hinterlege einen Ablageort wie deinen Nachbarn, die Filiale oder eine Paketstation, damit nicht mehrere Zustellungen versucht werden müssen.\n" +
                        "- Vermeide Rücksendungen, um unnötige Transportwege zu umgehen.\n" +
                        "\n" +
                        "Also wenn schon Online-Shopping, dann wenigstens nachhaltig!",
                date = Clock.System.now(),
                publishedAt = Clock.System.now(),
                tags = arrayListOf(
                    Tag("Dresden"), Tag("Leipzig")
                ),
                image = RemoteImage.of(
                    url = "https://loremflickr.com/1035/624",
                    size = Size(345f, 208f)
                ),
                teaser = "Du findest im Laden einfach nicht das, was du suchst? Online Shopping ist zwar grundsätzlich zweite Wahl, lässt sich aber leider nicht immer vermeiden.",
            ),
        )
    }
}
