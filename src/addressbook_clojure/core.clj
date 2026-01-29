(ns addressbook-clojure.core
  (:require [addressbook-clojure.db :as db]
            [addressbook-clojure.models :as models])
  (:gen-class))

(defn print-contact
  [{:keys [id first_name last_name phone email address]}]
  (println (format "[%3d] %s %s" id last_name first_name))
  (println (format "      Телефон: %s" phone))
  (when (seq email) (println (format "      Email:   %s" email)))
  (when (seq address) (println (format "      Адрес:   %s" address)))
  (println))

(defn list-contacts []
  (let [contacts (models/get-all-contacts)]
    (if (seq contacts)
      (do
        (println (format "\nВсего контактов: %d\n" (count contacts)))
        (doseq [c contacts] (print-contact c)))
      (println "\n📭 Список контактов пуст.\n"))))

(defn add-contact []
  (println "\n➕ Добавление нового контакта")
  (print "Фамилия: ") (flush)
  (let [last-name (read-line)
        first-name (do (print "Имя: ") (flush) (read-line))
        phone (do (print "Телефон: ") (flush) (read-line))
        email (do (print "Email (Enter для пропуска): ") (flush) (read-line))
        address (do (print "Адрес (Enter для пропуска): ") (flush) (read-line))]
    (when (or (empty? last-name) (empty? first-name) (empty? phone))
      (println "❌ Ошибка: Фамилия, имя и телефон обязательны!")
      (recur))
    (models/create-contact!
     {:first-name first-name
      :last-name last-name
      :phone phone
      :email (when (not= email "") email)
      :address (when (not= address "") address)})
    (println "✓ Контакт успешно добавлен!\n")))

(defn edit-contact []
  (print "\n✏️  Введите ID контакта для редактирования: ") (flush)
  (let [id-str (read-line)]
    (if (re-matches #"\d+" id-str)
      (let [id (Integer/parseInt id-str)
            contact (models/get-contact-by-id id)]
        (if contact
          (do
            (println "\nТекущие данные:")
            (print-contact contact)
            (println "Оставьте поле пустым, чтобы не изменять его.\n")

            (print (format "Фамилия [%s]: " (:last_name contact))) (flush)
            (let [last-name (read-line)
                  first-name (do (print (format "Имя [%s]: " (:first_name contact))) (flush) (read-line))
                  phone (do (print (format "Телефон [%s]: " (:phone contact))) (flush) (read-line))
                  email (do (print (format "Email [%s]: " (or (:email contact) ""))) (flush) (read-line))
                  address (do (print (format "Адрес [%s]: " (or (:address contact) ""))) (flush) (read-line))]

              (models/update-contact! id
                                      {:first-name (when (not= first-name "") first-name)
                                       :last-name (when (not= last-name "") last-name)
                                       :phone (when (not= phone "") phone)
                                       :email (cond
                                                (= email "") nil
                                                (empty? email) (:email contact)
                                                :else email)
                                       :address (cond
                                                  (= address "") nil
                                                  (empty? address) (:address contact)
                                                  :else address)})
              (println "✓ Контакт успешно обновлён!\n")))
          (println (format "❌ Контакт с ID %d не найден.\n" id))))
      (println "❌ Неверный формат ID. Введите число.\n"))))

(defn delete-contact []
  (print "\n🗑️  Введите ID контакта для удаления: ") (flush)
  (let [id-str (read-line)]
    (if (re-matches #"\d+" id-str)
      (let [id (Integer/parseInt id-str)
            contact (models/get-contact-by-id id)]
        (if contact
          (do
            (println "\nВы уверены, что хотите удалить этот контакт?")
            (print-contact contact)
            (print "Введите 'да' для подтверждения: ") (flush)
            (if (= (clojure.string/lower-case (read-line)) "да")
              (do
                (models/delete-contact! id)
                (println "✓ Контакт удалён!\n"))
              (println "❌ Удаление отменено.\n")))
          (println (format "❌ Контакт с ID %d не найден.\n" id))))
      (println "❌ Неверный формат ID. Введите число.\n"))))

(defn search-contacts []
  (print "\n🔍 Введите фамилию или имя для поиска: ") (flush)
  (let [query (read-line)
        results (models/search-contacts query)]
    (if (seq results)
      (do
        (println (format "\nНайдено %d контакт(ов):\n" (count results)))
        (doseq [c results] (print-contact c)))
      (println "\n❌ Ничего не найдено.\n"))))

(defn -main
  "Точка входа приложения"
  [& args]
  (db/init-db!)
  (println "╔════════════════════════════════════════╗")
  (println "║    АДРЕСНАЯ КНИГА (Clojure + SQLite)  ║")
  (println "╚════════════════════════════════════════╝")

  (loop []
    (println "\nМеню:")
    (println "  1. Показать все контакты")
    (println "  2. Добавить контакт")
    (println "  3. Редактировать контакт")
    (println "  4. Удалить контакт")
    (println "  5. Поиск по имени/фамилии")
    (println "  6. Выход")
    (print "\nВыберите действие [1-6]: ") (flush)

    (case (read-line)
      "1" (do (list-contacts) (recur))
      "2" (do (add-contact) (recur))
      "3" (do (edit-contact) (recur))
      "4" (do (delete-contact) (recur))
      "5" (do (search-contacts) (recur))
      "6" (println "\n👋 До свидания!")
      (do (println "⚠️  Неверный выбор, попробуйте снова.") (recur)))))