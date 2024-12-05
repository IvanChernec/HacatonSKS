<h1>Содержание репозитория:</h1>
<ul>
  <li>Web - клиент Web-версии;
  <li>API - серверная часть;
  <li>Android - клиент мобильной версии.
</ul>
<h1>Сервер</h1>
Перед запуском сервера установить языковой пакет Python v3.10.x.
<br>Для запуска сервера нужно распаковать архив находящийся в API. После запустить консоль и перейти в разархивированную папку и прописать команду "pip install django", для установки django.
<br>Далее необходимо запустить виртуальное окружение командой ".venv\Scripts\activate.bat".
<br>После чего прописать следующую команду "python manage.py runserver [ip-адрес:порт]".
<br>На этом настройка сервера закончена.
<br>Примечание: в файле settings.py в переменную ALLOWED_HOSTS необходимо добавить список разрешённых для подключения ip адресов на которых будет запущен сервер.
<h1>Web-клиент</h1>
Для запуска клиента нужно открыть инсполняемый файл "WebAPI.exe".
<h1>Android</h1>
Для запуска мобильного клиента установить приложение с помощью файла app-debug.apk.
