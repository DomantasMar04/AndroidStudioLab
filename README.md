# Android laboratoriniai darbai 2026

## Lab 1 – AVD ir telnet

Empty Views Activity projektas (Java). `activity_main.xml` faile TextView tekstas pakeistas į vardą ir pavardę. Programa paleista AVD emuliatoriuje.

### Telnet paleidimas
1. Įjungti: Windows features → ☑ Telnet Client
2. Prisijungti: `telnet localhost 5554`
3. Gauti token'ą: `cat ~/.emulator_console_auth_token` (Git Bash)
4. Autentifikuotis: `auth <token>`

### Komandos
```
sms send 861234567 Labas    # SMS
gsm call 861234567          # skambutis
gsm cancel 861234567        # atšaukti
power capacity 10           # baterija 10%
quit                        # atsijungti
```

![Rezultatas](Lab01/screenshots)