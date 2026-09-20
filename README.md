# PvP Helper (Fabric 1.21.4)

- Wypelniony hitbox innych graczy (zolty, gdy celownik jest na graczu = trafisz).
- Klawisz `\` otwiera menu: wl./wyl. hitbox, kolor, przezroczystosc, auto-hit.
- Auto-hit: 1 uderzenie gdy trzymasz miecz i jest w pelni naladowany, potem czeka na kolejne naladowanie.
- Prawdziwy hitbox NIE jest powiekszany.

## Jak dostac .jar
### Opcja A - GitHub (bez instalowania czegokolwiek)
1. Zaloz repo na github.com i wrzuc CALA zawartosc tego folderu (razem z .github/).
2. Zakladka **Actions** -> workflow **build** -> po ~2 min otworz przebieg -> **Artifacts** -> pobierz `Artifacts`.
3. W srodku jest `pvphelper-1.0.0.jar` (NIE ten z `-sources`).

### Opcja B - lokalnie
Java 21+ i w tym folderze: `./gradlew build` (Windows: `gradlew.bat build`).
Jar: `build/libs/pvphelper-1.0.0.jar`.

## Instalacja
Fabric Loader + Fabric API 0.119.4+1.21.4 -> wrzuc `pvphelper-1.0.0.jar` do `.minecraft/mods`.
