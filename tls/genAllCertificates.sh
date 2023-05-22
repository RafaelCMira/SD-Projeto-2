#!/bin/bash

# Lista de servidores
servidores=("users0-ourorg0" "feeds0-ourorg0" "feeds1-ourorg0" "feeds2-ourorg0" "users0-ourorg1" "feeds0-ourorg1" "feeds1-ourorg1" "feeds2-ourorg1" "users0-ourorg2" "feeds0-ourorg2" "feeds1-ourorg2" "feeds2-ourorg2")

# Loop para criar as keystores para cada servidor
for servidor in "${servidores[@]}"
do
    # Define o nome da keystore e a senha
    nome_keystore="${servidor}.jks"
    password="${servidor}123"

    # Gera um novo arquivo de armazenamento de chaves
    keytool -genkey -alias "$servidor" -keyalg RSA -validity 365 -keystore "./$nome_keystore" -storetype pkcs12 -ext SAN=dns:"$servidor" << EOF
$password
$password
$servidor
TP2
SD2223
LX
LX
PT
yes
$password
$password
EOF

    echo
    echo
    echo "Exportando Certificados"
    echo
    echo

    # Exporta o certificado para um arquivo
    keytool -exportcert -alias "$servidor" -keystore "$nome_keystore" -file "$servidor.cert" << EOF
$password
EOF

    echo "Criando Truststore do Cliente"
    cp cacerts client-ts.jks

    # Importa o certificado para o truststore do cliente
    keytool -importcert -file "$servidor.cert" -alias "$servidor" -keystore client-ts.jks << EOF
changeit
yes
EOF
done
