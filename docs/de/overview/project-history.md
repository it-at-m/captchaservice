# Projektgeschichte

## Kontext

**CaptchaService** wurde als Bot-Schutzschicht vor den öffentlichen ZMS-/eAppointment-APIs der Landeshauptstadt München entwickelt — konkret vor [`zmscitizenview`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenview) (dem Bürger-Frontend für die Terminbuchung) und [`zmscitizenapi`](https://github.com/it-at-m/eappointment/tree/main/zmscitizenapi) (dem REST-Backend, das die freien Termine ausliefert).

Im Laufe der Jahre durchlief das Team mehrere Iterationen, um automatisierte Scraper und Buchungs-Bots von diesen Endpunkten fernzuhalten:

1. **Eine einfache, selbst entwickelte CAPTCHA-Lösung** — die erste Verteidigungslinie. Betrieblich überschaubar, aber das Bot-Umfeld blieb in Bewegung und die hauseigene Lösung musste immer wieder nachgehärtet werden.
2. **Kommerzielle, proprietäre CAPTCHA-Dienste** — funktionierten im Produktivbetrieb gut, waren als Closed-Source-Angebote mit externem, DSGVO-konformem EU-Hosting für eine öffentliche Verwaltung jedoch letztlich nicht die passendste Lösung.
3. **[ALTCHA](https://altcha.org/)** — sobald eine vergleichbare, vollständig quelloffene Proof-of-Work-Bibliothek gefunden war, sind wir umgestiegen. ALTCHA ist DSGVO-konform by design — keine Cookies, kein Tracking, keine Aufrufe an Drittanbieter —, liefert dieselbe Art nahezu unsichtbarer Client-Challenge und wird [in Europa entwickelt](https://altcha.org/). Code, Protokoll und Server können vollständig in-house unter MIT betrieben werden.

CaptchaService ist der kleine, mandantenfähige Spring-Boot-Dienst, der ALTCHA heute kapselt und allen Münchner Anwendungen eine einzige CAPTCHA-Schnittstelle bereitstellt.

Eine quelloffene, europäische Bibliothek zu wählen, ist für uns kein Zufall: Als öffentliche Verwaltung wollen wir aktiv die **digitale Souveränität** stärken — kritische Infrastruktur soll auf einem Stack laufen, den wir lesen, selbst hosten, prüfen und aktiv mitgestalten können.

## Was wird geschützt

Im eAppointment-/ZMS-Stack sitzt CaptchaService heute vor den Endpunkten zur Slot-Suche von `zmscitizenapi`. Eine Challenge wird angefordert, sobald die Bürgerin oder der Bürger die Buchung in `zmscitizenview` öffnet; den verifizierten Payload verlangt `zmscitizenapi` dann, bevor eine Reservierung oder ein Scraping-ähnlicher Lesezugriff zugelassen wird.

Das Netto-Ergebnis:

- Bots können nicht mehr günstig die verfügbaren Termine in `zmscitizenapi` auflisten.
- Echte Nutzer:innen sehen in `zmscitizenview` nur einen nahezu unsichtbaren Proof-of-Work-Schritt.
- Keine personenbezogenen Daten verlassen die Münchner Infrastruktur.

## Herkunft

CaptchaService wurde bei **it@M**, dem IT-Dienstleister der Landeshauptstadt München, entwickelt.

Das Repository basiert auf [`it-at-m/refarch-templates`](https://github.com/it-at-m/refarch-templates) und folgt damit denselben Konventionen, Workflows und Verhaltenskodizes wie die anderen Münchner Referenzarchitektur-Projekte.

## Heute

- **Im produktiven Einsatz**: vor `zmscitizenapi` / `zmscitizenview` im ZMS-/eAppointment-Projekt.
- **Wiederverwendbar**: jedes Spring-Boot- oder anderssprachiges Frontend kann den Dienst über HTTP aufrufen, eine eigene Site mit Schlüssel/Geheimnis konfigurieren und eine eigene Schwierigkeits-Map nutzen.
- **Open Source**: veröffentlicht unter MIT auf [GitHub](https://github.com/it-at-m/captchaservice) und auf [opensource.muenchen.de](https://opensource.muenchen.de/).
