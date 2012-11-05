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
