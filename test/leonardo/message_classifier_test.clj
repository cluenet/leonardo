(ns leonardo.message-classifier-test
  (:use [clojure.test]
        [leonardo.message-classifier]))

(defmacro classifier-test
  [name classifier sentence]
  `(deftest ~name
     (let [sentence# ~sentence
           classifiers# (classify-sentence sentence#)]
       (is (contains? classifiers# ~classifier)))))

(classifier-test clueful-sentence-test :clueful "This is a clueful sentence.")
(classifier-test normal-sentence-test :normal "This is a normal sentence")
(classifier-test abnormal-sentence-test :abnormal "this is an abnormal sentence")