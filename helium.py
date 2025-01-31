pip install python-telegram-bot gspread oauth2client
import logging
from telegram import Update
from telegram.ext import Updater, CommandHandler, MessageHandler, Filters, CallbackContext
import gspread
from oauth2client.service_account import ServiceAccountCredentials

# Настройка логирования
logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)

# Google Sheets настройки
SCOPE = ["https://spreadsheets.google.com/feeds", "https://www.googleapis.com/auth/drive"]
CREDS_FILE = 'path/to/your/credentials.json'  # Путь к вашему JSON-файлу с ключами
SPREADSHEET_ID = '1_3ipM7yV6UDv7UVa1xaHDapmBgjWOWFnzdkGKYFm1-c'  # ID вашей таблицы
RANGE_NAME = 'Sheet1!A1'  # Имя листа и диапазон

# Функция для добавления данных в Google Таблицу
def add_to_google_sheet(data):
    credentials = ServiceAccountCredentials.from_json_keyfile_name(CREDS_FILE, SCOPE)
    client = gspread.authorize(credentials)
    sheet = client.open_by_key(SPREADSHEET_ID).sheet1  # Открываем первый лист
    sheet.append_row(data)  # Добавляем строку данных

# Команда /start
def start(update: Update, context: CallbackContext):
    update.message.reply_text("Привет! Отправь мне данные, которые нужно записать в Google Таблицу.")

# Обработка текстовых сообщений
def handle_message(update: Update, context: CallbackContext):
    user_data = update.message.text.split(',')
    add_to_google_sheet(user_data)
    update.message.reply_text("Данные успешно добавлены в Google Таблицу!")

# Основная функция
def main():
    # Вставьте свой токен API здесь
    updater = Updater("7735363342:AAEM034-183sngwTYZ569PxwJPaGQsjy4pY")

    # Получаем диспетчер для регистрации обработчиков
    dp = updater.dispatcher

    # Регистрация обработчиков команд и сообщений
    dp.add_handler(CommandHandler("start", start))
    dp.add_handler(MessageHandler(Filters.text & ~Filters.command, handle_message))

    # Запуск бота
    updater.start_polling()
    updater.idle()
