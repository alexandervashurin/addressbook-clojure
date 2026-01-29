(defproject addressbook-clojure "0.1.0-SNAPSHOT"
  :description "Адресная и телефонная книга на Clojure + SQLite"
  :url "https://github.com/alexandervashurin/addressbook-clojure"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.xerial/sqlite-jdbc "3.44.1.0"]
                 [org.clojure/string "1.1.0"]]  ;; Добавлена зависимость для работы со строками
  :main ^:skip-aot addressbook-clojure.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})