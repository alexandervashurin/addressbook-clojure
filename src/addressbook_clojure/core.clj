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
    (models/create-contact!
      {:first-name first-name
       :last-name last-name
       :phone phone
       :email (when (not= email "") email)
       :address (when (not= address "") address)})
    (println "✓ Контакт успешно добавлен!\n")))

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
    (println "  3. Поиск по имени/фамилии")
    (println "  4. Выход")
    (print "\nВыберите действие [1-4]: ") (flush)

    (case (read-line)
      "1" (do (list-contacts) (recur))
      "2" (do (add-contact) (recur))
      "3" (do (search-contacts) (recur))
      "4" (println "\n👋 До свидания!")
      (do (println "⚠️  Неверный выбор, попробуйте снова.") (recur)))))
