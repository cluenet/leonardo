(ns leonardo.bot
  (:require [irclj.core :as ircb]
            [irclj.events :as irce]
            [leonardo.message-scorer :as s]
            [leonardo.message-classifier :as c]
            [leonardo.users :as u]))

(def users (ref (u/init-users)))

(defn add-user-maybe
  [users nick]
  (if (users nick)
    users
    (u/add-user users nick)))

(defn incr-reasons
  [users nick reasons]
  (reduce #(u/incr-reason %1 nick %2) users reasons))

(defn score-message
  [users m]
  (let [message (:text m)
        nick (:nick m)
        score (s/score-sentence message)
        reasons (c/classify-sentence message)]
    (-> users
        (add-user-maybe nick)
        (u/incr-points nick score)
        (incr-reasons nick reasons))))

(defn privmsg
  [irc m]
  (let [message (:text m)
        nick (:nick m)
        score (s/score-sentence message)
        reasons (c/classify-sentence message)]
    (dosync
     (alter users score-message m))
    (println @users)))

(defn make-bot
  []
  (ircb/connect "irc.cluenet.org" 6667
                "Leonardo"
                :real-name "Leonardo da Vinci"
                :callbacks {:privmsg privmsg
                            :raw-log irce/stdout-callback}))

(defn start-bot
  []
  (let [irc (make-bot)]
    (ircb/join irc "#leonardo")))