NAJPROSTSZY SPOSOB ZBUDOWANIA JAR-A BEZ INSTALOWANIA MAVENA NA KOMPUTERZE

1. Zaloguj sie lub zaloz konto na https://github.com
2. Utworz nowe repozytorium (New repository), np. AutoCobwebRemove.
3. Rozpakuj ZIP na komputerze.
4. W repozytorium wybierz Add file -> Upload files.
5. Przeciagnij WSZYSTKIE pliki i foldery z rozpakowanego projektu, w tym ukryty folder .github.
6. Kliknij Commit changes.
7. Wejdz w zakladke Actions.
8. Wybierz "Build AutoCobwebRemove" i kliknij "Run workflow".
9. Po zakonczeniu zielonego zadania otworz jego szczegoly.
10. Na dole w sekcji Artifacts pobierz "AutoCobwebRemove-JAR".
11. Rozpakuj pobrany artifact ZIP. W srodku bedzie AutoCobwebRemove-1.0.0.jar.
12. Wgraj ten JAR do plugins na IVhost i zrestartuj serwer.

UWAGA: GitHub musi otrzymac caly projekt, lacznie z folderem .github/workflows.
