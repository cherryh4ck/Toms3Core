# Toms3Core
### Core creado para toms3.cc
### Contiene funcionalidades únicas que pueden servir en distintos servidores anárquicos. 
[![Toms3](https://img.shields.io/badge/usado_en-toms3.cc-green)](https://www.toms3.cc) [![Discord](https://img.shields.io/discord/1466280177335013533?label=Discord&color=5865F2&logo=discord)](https://discord.gg/toms3)
___

## Funcionalidades
* Comando de joindate
* Comando de playtime
* Comandos troll para los newfags (por ej: /dupe)
* Conexión a una webhook de Discord para loggear quienes caen en los comandos troll
* Buscar jugador por UUID o viceversa (solo administradores)
* MOTD personalizable al entrar al servidor
* MOTD personalizable al entrar al servidor por primera vez
* Previene el acceso al techo del Nether sin matar al jugador
* Parche para el "vclip exploit", usado para entrar y viajar debajo de la bedrock en el nether
___

## Comandos
* /tmcore
* /tmcore reload
* /tmcore help
* /tmcore illegal_test
* /tmcore get_player_by_uuid
* /tmcore get_uuid_by_player
* /joindate (/jd)
* /playtime (/pt)
* /dupe
___

## BUGS
* /joindate y /playtime no funcionan en jugadores con UUID premium si el servidor es mixto (uuids cracked y premium)
___

## TODO
* Implementar sistema de cache para joindate y playtime
* Límite de contenedores (o tile entities) por chunk simplemente cancelando la acción del jugador (alternativa a AEF)
* Announcer
* Announcer de título al unirse
* Cancelar redirects (?
