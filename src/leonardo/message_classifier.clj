(ns leonardo.message-classifier)

(def clueful-regex #"^([^ ]+(:|,| -) .|[^a-z]).*(\?|\.(`|'|\")?|!|:)$")
(def normal-regex #"^([^ ]+(:|,| -) .|[^a-z]).*$")

(defn- sentence-type
  ([sentence]
     (cond
      (re-find clueful-regex sentence) :clueful
      (re-find normal-regex sentence) :normal
      :else :abnormal)))

(def classifier-descriptions
  {:clueful "Clueful sentence"
   :normal "Normal sentence"
   :abnormal "Abnormal sentence"
   :personal-pronoun "Lower-case personal pronoun"
   :uncreative-profanity "Use of uncreative profanity"
   :all-caps "All caps"
   :profanity "Use of profanity"
   :lawl "Use of non-clueful variation of \"lol\""
   :rawr "Use of non-clueful expression"
   :no-vowels "No vowels"
   :r-u "Use of r, R, u or U"})

(def sentence-classifiers
  {:personal-pronoun #"(^| )i( |$)"
   :uncreative-profanity #"(?i:\b(?:crap|cunt|fuck|shit)\b)"
   :all-caps #"^[^a-z]{8,}$"
   :profanity #"\<censored\>"
   :lawl #"(^| )lawl( |$)"
   :rawr #"(^| )rawr( |$)"
   :no-vowels #"(?i:^[^aeiouy]+$)"
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

