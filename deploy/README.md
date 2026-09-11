# Автодеплой на droplet

Пуш у `main` → GitHub Actions (`.github/workflows/deploy.yml`) збирає jar, копіює його на
`root@157.230.127.148:/opt/app/underwearshop.jar` і рестартує systemd-сервіс `underwearshop`.

## Одноразове налаштування (виконати самостійно, у мене нема SSH-доступу до дроплета)

### 1. Deploy-ключ (окремий від особистого, щоб можна було відкликати без втрати доступу)

Локально:
```bash
ssh-keygen -t ed25519 -f deploy_key -N ""
```

Додати публічний ключ на дроплет:
```bash
ssh-copy-id -i deploy_key.pub root@157.230.127.148
# або вручну: cat deploy_key.pub | ssh root@157.230.127.148 "cat >> ~/.ssh/authorized_keys"
```

(Можна й перевикористати вже наявний приватний ключ, яким ви зараз ходите на сервер, —
просто менш ізольовано, якщо колись знадобиться відкликати доступ тільки CI.)

### 2. Systemd-сервіс на дроплеті

Зупинити поточний nohup-процес і поставити сервіс замість нього:
```bash
ssh root@157.230.127.148
pkill -f underwearshop   # або узнати pid: ps aux | grep java
```

Скопіювати `deploy/underwearshop.service` з цього репозиторію на сервер (шлях `/opt/app/underwearshop.jar`
вже відповідає тому, що зараз використовується):
```bash
scp deploy/underwearshop.service root@157.230.127.148:/etc/systemd/system/underwearshop.service
```

На сервері:
```bash
systemctl daemon-reload
systemctl enable --now underwearshop
systemctl status underwearshop      # перевірити, що запустився
journalctl -u underwearshop -f      # логи, як раніше в консолі nohup
curl -s localhost:8080/api/category # sanity check
```

### 3. GitHub Secrets

У репозиторії `mark405/underwearbackend` → Settings → Secrets and variables → Actions → New repository secret:

| Secret            | Значення                                              |
|--------------------|--------------------------------------------------------|
| `DEPLOY_HOST`      | `157.230.127.148`                                       |
| `DEPLOY_USER`      | `root`                                                  |
| `DEPLOY_SSH_KEY`   | вміст приватного ключа (файл `deploy_key`, весь текст, включно з `-----BEGIN...` / `-----END...`) |

### 4. Перевірка

Запушити будь-який коміт у `main` → вкладка Actions на GitHub → job `deploy` має пройти зелено →
`systemctl status underwearshop` на сервері покаже свіжий `Active: active (running)` час старту.

## Нотатка про секрети в application.properties

`src/main/resources/application.properties` зараз закомічений у git разом з реальними секретами
(пароль БД, SendGrid API key, Telegram bot token, `ADMIN_LOGIN`/`ADMIN_PASSWORD=ADMIN`). Це не
пов'язано напряму з автодеплоєм і я це не чіпав, але раз тепер є CI - варто винести їх у GitHub
Secrets + переменные оточення на сервері (`application.properties` тоді читає `${VAR_NAME}`), а не
тримати в репозиторії. Скажіть, якщо хочете, щоб я це зробив окремим кроком.
