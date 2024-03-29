#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(def targets
  (->> (bash "ls **/index.org | grep -v '^index.org$' | sed 's|/index.org||g'")
       (str/split-lines)))

(defn org [target]
  (str target "/index.org"))

(defn html [target]
  (str target "/index.html"))

;; Generate target for root index
(println (str/join " " (concat ["index.html:" "index.clj"] (map html targets))))
(println "\t./index.clj")
(println "")

(def css false)

;; Generate target for each page
(println
 (str/join "\n\n"
           (for [t targets]
             (str (html t) ": " (org t)
                  "\n\t"
                  "pandoc -s --toc --from=org+smart -i " (org t)
                  (if css
                    " --css=../pandoc.css"
                      "")
                  " -o " (html t)))))

;; Generate phony target for pages
(println "")
(println "@PHONY: pages")
(println "pages: " (str/join " " (map html targets)))
(println "")
