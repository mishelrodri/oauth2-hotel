#Pasos

Antes de iniciar el proyecto copiar la llave generada por el auth-server con jdk15
y pegarla en el bean sino estara dando error 401, la configuracion del yml da igual si se pone o se quita XD
al final el Bean hace la magia
Interesante que si queremos probrar que pida token solo es de configurar el cliente y el ira al endpoint
pero eso ya es otra cosa.. que ni probe pero si lo lei entre tanta info


Por cierto al inicio pense que solo poniendo el string de la llave publica en `key-value` habia funcionado
pero nooo al final el que esta haciendo que funcione es el JwtDecoderConfig xd