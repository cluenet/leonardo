(ns leonardo.message-classifier)

(def clueful-regex #"^([^ ]+(:|,| -) .|[^a-z]).*(\?|\.(`|'|\")?|!|:)$")
(def normal-regex #"^([^ ]+(:|,| -) .|[^a-z]).*$")

(defn- sentence-type
  ([sentence]
     (cond
      (re-matches clueful-regex sentence) :clueful
      (re-matches normal-regex sentence) :normal
      :else :abnormal)))

(defn classify-sentence
  ([sentence]
     (conj #{} (sentence-type sentence))))

