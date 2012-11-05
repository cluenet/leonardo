(ns leonardo.message-scorer-test
  (:use [clojure.test]
        [leonardo.message-scorer]))

(defmacro score-test
  [name score sentence]
  `(deftest ~(symbol (format "%s-test" name))
     (let [sentence# ~sentence
           score# (score-sentence sentence#)]
       (is (= ~score score#)))))

(score-test clueful-sentence
            2 "This is a clueful sentence.")
(score-test all-caps
            -19 "THIS IS A SENTENCE")