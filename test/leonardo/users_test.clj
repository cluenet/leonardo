(ns leonardo.users-test
  (:use [clojure.test]
        [leonardo.users]))

(def users (init-users))

(deftest add-user-test
  "Adding a user sets their points to 0"
  (let [users (add-user users "johndoe")]
    (is (= 0 (get-points users "johndoe")))))

(deftest set-points-test
  (let [users (-> users
                  (add-user "johndoe")
                  (set-points "johndoe" 150))]
    (is (= 150 (get-points users "johndoe")))))

(deftest incr-points-test
  (let [users (-> users
                  (add-user "johndoe")
                  (set-points "johndoe" 500)
                  (incr-points "johndoe" 15))]
    (is (= 515 (get-points users "johndoe")))))

(deftest incr-reason-test
  (let [users (-> (add-user users "johndoe")
                  (incr-reason "johndoe" :clueful)
                  (incr-reason "johndoe" :clueful))]
    (is (= 2 (reason-count users "johndoe" :clueful)))))

(deftest toggle-flag-test
  (let [users (-> (add-user users "johndoe")
                  (toggle-flag "johndoe" :verbose))]
    (is (flag-enabled? users "johndoe" :verbose))))