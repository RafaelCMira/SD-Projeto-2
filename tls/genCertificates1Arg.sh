#!/bin/bash

# Verifica se foi passado o nome do servidor como argumento
if [ "$#" -ne 1 ]; then
    echo "Por favor, forneça o nome do servidor como argumento."
    exit 1
fi

# Atribui o argumento ao nome do servidor
nome_servidor=$1

# Define o nome da keystore e a senha
nome_keystore="${nome_servidor}.jks"
password="${nome_servidor}123"

# Remove todos os arquivos com extensão ".jks"
rm -f *.jks

# Gera um novo arquivo de armazenamento de chaves
keytool -genkey -alias "$nome_servidor" -keyalg RSA -validity 365 -keystore "./$nome_keystore" -storetype pkcs12 << EOF
$password
$password
$nome_servidor
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
keytool -exportcert -alias "$nome_servidor" -keystore "$nome_keystore" -file "$nome_servidor.cert" << EOF
$password
EOF

echo "Criando Truststore do Cliente"
cp cacerts client-ts.jks

# Importa o certificado para o truststore do cliente
keytool -importcert -file "$nome_servidor.cert" -alias "$nome_servidor" -keystore client-ts.jks << EOF
changeit
yes
EOF
