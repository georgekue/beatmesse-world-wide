insert into mesg values(default, CURRENT_TIMESTAMP, null, -1, 'SMS', '1234', 'Gott, lasse das Internet nicht Gott-freier Raum sein. Hilf uns, Dich überall zu finden, nicht nur hier in der Johanneskirche.');
insert into mesg values(default, CURRENT_TIMESTAMP, null, -1, 'SMS', '1234', 'Gott, stehe den Opfern von Mobbing, Missbrauch und Gewalt im Internet bei. Hilf uns, Menschen in Not zu erkennen und ihnen eine helfende Hand zu reichen.');
insert into mesg values(default, CURRENT_TIMESTAMP, null, -1, 'SMS', '1234', 'Gott, das Internet ist cool, aber Deine reale Welt ist cooler! Hilf uns, dass wir uns nicht im Weltweit verlieren.');
insert into mesg values(default, CURRENT_TIMESTAMP, null, -1, 'SMS', '1234', 'Gott, Kinder brauchen jemanden, der *immer* für sie da ist. Das Internet darf nicht zur Ausrede werden, dass das angeblich nicht gehe.');
insert into mesg values(default, CURRENT_TIMESTAMP, null, -1, 'SMS', '1234', 'Hamburg - Neuer Ärger für Apple: Justizministerin Sabine Leutheusser-Schnarrenberger (FDP) wirft dem US-Konzern vor, ihrem Ministerium gegenüber falsche Angaben gemacht zu haben. "Ich habe bereits vor einem Jahr mehr Transparenz bei der Speicherpraxis von personenbezogenen Daten von Apple gefordert", sagte die Ministerin SPIEGEL ONLINE. In einem Schreiben an das Ministerium versicherte Apple-Lobbyistin Claire Thwaites am 29. Juli 2010 , dass insbesondere Standortdaten nur nach ausdrücklicher Zustimmung der Nutzer gespeichert würden. "Nun stellt sich heraus, dass dies eben nicht der Fall war", so Leutheusser-Schnarrenberger. Zwei Entwickler hatten im April demonstriert, dass Apples iPhone fortlaufend Positionsdaten sammelt und über längere Zeit speichert. Das Unternehmen hatte das nach einer Woche des Schweigens als Programmierfehler abgetan, aber auch zugegeben, dass das Telefon Positionsdaten auch dann sammelt und an Apple sendet, wenn die Nutzer die Ortungsdienste explizit abschalten. Die Ministerin will das nicht gelten lassen: "Wenn Apple nun erklärt, die Speicherung der sensiblen Daten sei nur durch einen Programmierfehler zustande gekommen, muss das Unternehmen auch erläutern, wie es derartige Fehler zukünftig vermeiden will", so Leutheusser-Schnarrenberger.');

update conf set value = 10 where attr = 'display_delay';

select MESSAGE from mesg where id = 194;
select count(*) from mesg ;
select * from mesg;
select count (*) from mesg where displaycnt in (0, 1, 2, 3, 4);
select count (*) from mesg where displaycnt in (1);

update mesg set displaycnt = 1 where displaycnt > 1;
