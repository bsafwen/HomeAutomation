# HomeAutomation
Consultation des états des équipements:
Un capteur détecte un événement, envoie l'information au microprocesseur de la carte Arduino qui la transmet au module wifi ESP8266 via la communication en série. Ce dernier va envoyer le message à la centrale des capteurs à travers un autre module ESP8266, la centrale transmet le message cette fois ci en utilisant le protocole MQTT vers la carte Raspberry Pi qui est le serveur. Le message étant bien reçu, le serveur détermine s'il y a une alerte à déclencher et déclenche si nécessaire et envoie finalement le message aux clients souscrits. 

Contrôle des équipements:
L'utilisateur peut changer l'état d'un équipement depuis son smartphone. Le message est transmis au serveur qui l'envoie à la centrale des capteurs, qui se chargera à envoyer le message à la carte Arduino à laquelle est connecté cet équipement. A l'arrivée du message, la carte Arduino teste la validité du message et exécute la commande demandée par l'utilisateur(par exemple: Allumer une lampe).
