(ns leonardo.users)

(defn init-users [] {})

(defn user-exists?
  [users username]
  (contains? users username))

(defn reset-user
  [users username]
  (conj users
        {username
         {:points 0
          :reasons {}
          :flags #{}}}))

(defn add-user
  [users username]
  (if (user-exists? users username)
    users
    (reset-user users username)))

(defn get-points
  [users username]
  (or (:points (users username)) 0))

(defn set-points
  [users username points]
  (assoc-in users [username :points] points))

(defn incr-points
  [users username delta]
  (update-in users [username :points] + delta))

(defn- create-reason
  [users username reason]
  (if (reason (:reasons (users username)))
    users
    (assoc-in users [username :reasons reason] 0)))

(defn incr-reason
  [users username reason]
  (update-in (create-reason users username reason)
             [username :reasons reason] inc))

(defn incr-reasons
  [users username reasons]
  (reduce #(incr-reason %1 username %2) users reasons))

(defn reason-count
  [users username reason]
  (reason (:reasons (users username))))

(defn enable-flag
  [users username flag]
  (update-in users [username :flags] conj flag))

(defn disable-flag
  [users username flag]
  (update-in users [username :flags] disj flag))

(defn flag-enabled?
  [users username flag]
  (contains? (:flags (users username)) flag))

(defn toggle-flag
  [users username flag]
  (if (flag-enabled? users username flag)
    (disable-flag users username flag)
    (enable-flag users username flag)))

