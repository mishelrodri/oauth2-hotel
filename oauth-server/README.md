# Modulo para integrarse a los servcios

Este modulo/proyecto esta pensado para facilitar la implementacion de la configuración de endpoints públicos en los 
microservicios, contiene la configuracion necesaria para hacer publicos los endpoints asi como tambien es el encargado
de autoconfigurar la seguridad, es decir ya sabe donde esta el auth-server para verificar los tokens, etc

Importante antes de poder iniciar los microservicios correr estos comandos en este proyecto 

```shell
mvn clean install
```