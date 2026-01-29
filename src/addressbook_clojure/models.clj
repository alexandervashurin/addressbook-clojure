(ns addressbook-clojure.models
  (:require [addressbook-clojure.db :as db]
            [clojure.java.jdbc :as jdbc]))

(defn create-contact!
  "Создаёт новый контакт"
  [{:keys [first-name last-name phone email address]}]
  (jdbc/insert! db/db-spec :contacts
                {:first_name first-name
                 :last_name last-name
                 :phone phone
                 :email email
                 :address address}))

(defn get-all-contacts
  "Получает все контакты, отсортированные по фамилии"
  []
  (jdbc/query db/db-spec
              ["SELECT * FROM contacts ORDER BY last_name, first_name"]))

(defn get-contact-by-id
  "Получает контакт по ID"
  [id]
  (first (jdbc/query db/db-spec
                     ["SELECT * FROM contacts WHERE id = ?" id])))

(defn search-contacts
  "Поиск контактов по фамилии или имени"
  [query]
  (let [pattern (str "%" query "%")]
    (jdbc/query db/db-spec
                ["SELECT * FROM contacts 
        WHERE last_name LIKE ? OR first_name LIKE ? 
        ORDER BY last_name, first_name"
                 pattern pattern])))

(defn update-contact!
  "Обновляет контакт по ID. nil-значения игнорируются (поле не изменяется)"
  [id {:keys [first-name last-name phone email address]}]
  (let [updates (into {}
                      (remove #(nil? (val %))
                              {:first_name first-name
                               :last_name last-name
                               :phone phone
                               :email email
                               :address address}))]
    (when (seq updates)
      ;; ВАЖНО: без apply! jdbc/update! принимает мапу напрямую
      (jdbc/update! db/db-spec :contacts updates ["id = ?" id]))))

(defn delete-contact!
  "Удаляет контакт по ID"
  [id]
  (jdbc/delete! db/db-spec :contacts ["id = ?" id]))