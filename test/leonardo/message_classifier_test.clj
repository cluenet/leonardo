(ns leonardo.message-classifier-test
  (:use [clojure.test]
        [leonardo.message-classifier]))

(defmacro classifier-test
  [classifier sentence]
  `(deftest ~(symbol (format "%s-test" (name classifier)))
     (let [sentence# ~sentence
           classifiers# (classify-sentence sentence#)]
       (is (contains? classifiers# ~classifier)))))

(classifier-test :clueful
                 "This is a clueful sentence.")
(classifier-test :normal
                 "This is a normal sentence")
(classifier-test :abnormal
                 "this is an abnormal sentence")

(classifier-test :personal-pronoun
                 "i use the lower-case personal pronoun")
(classifier-test :uncreative-profanity
                 "Fuck this.")
(classifier-test :all-caps
                 "I LIKE TO SHOUT.")
(classifier-test :profanity
                 "<censored> printer")
(classifier-test :lawl
                 "I lawl at this thing.")
(classifier-test :rawr
                 "I rawr at this thing.")
(classifier-test :no-wovels
                 "Ths s sntnc wtht vwls.")
(classifier-test :r-u
                 "R u feeling good today?")
