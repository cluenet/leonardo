(ns leonardo.message-classifier)

(def clueful-regex #"^([^ ]+(:|,| -) .|[^a-z]).*(\?|\.(`|'|\")?|!|:)$")
(def normal-regex #"^([^ ]+(:|,| -) .|[^a-z]).*$")

(defn- sentence-type
  ([sentence]
     (cond
      (re-find clueful-regex sentence) :clueful
      (re-find normal-regex sentence) :normal
      :else :abnormal)))

(def sentence-classifiers
  {:personal-pronoun #"(^| )i( |$)"
   :uncreative-profanity #"(?i:\b(?:crap|cunt|fuck|shit)\b)"
   :all-caps #"^[^a-z]{8,}$"
   :profanity #"\<censored\>"
   :lawl #"(^| )lawl( |$)"
   :rawr #"(^| )rawr( |$)"
   :no-wovels #"(?i:^[^aeiouy]+$)"
   :r-u #"(?i:(^| )[ru]( |$))"})

(defn- sentence-classifiers-for-sentence
  [sentence]
  (keys (filter
         (fn [[_ regex]] (re-find regex sentence))
         sentence-classifiers)))

(defn classify-sentence
  ([sentence]
     (conj
      (set (sentence-classifiers-for-sentence sentence))
      (sentence-type sentence))))

