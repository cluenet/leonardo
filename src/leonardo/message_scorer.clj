(ns leonardo.message-scorer
  (:require [leonardo.message-classifier :as classifier]))

(def scores-for-classifiers
  {:clueful 2
   :normal 1
   :abnormal -1
   :personal-pronoun -5
   :uncreative-profanity -20
   :all-caps -20
   :profanity -20
   :lawl -20
   :rawr -20
   :no-vowels -30
   :r-u -40})

(defn score-sentence
  [sentence]
  (reduce + (map #(get scores-for-classifiers % 0)
                 (classifier/classify-sentence sentence))))