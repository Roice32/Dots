# Dots
Permite crearea de grafuri neorientate și utilizarea unui algoritm A* pentru găsirea celui mai scurt drum dintre 2 noduri.
Îmbunătățiri față de versiunea UNOPTIMIZED: \n
  -> Nu mai apar artefacte vizuale: bucăți de noduri/muchii lipsă după operații de ștergere.
  -> Nodurile & muchiile sunt memorate în HashMap-uri: scapă de limitarea de maxim 100 noduri & muchii.
  -> Pathfinding cu algoritmul lui Dijkstra: Găsește CEL MAI SCURT drum de la un nod la altul, și nu unul oarecare.
  -> Nu mai trimite NullPointerException-uri.
