#!/bin/bash

cd "$(dirname "$0")"

echo -e "\n[\e[34mINFO\e[0m] Compilando o projeto..."
mvn -f ../pom.xml compile -q

echo "------------------------------------------"
echo "Iniciando Bateria de Testes"
echo "------------------------------------------"

if [ -d "test_results" ]; then
    rm -rf test_results/*
else
    mkdir -p test_results
fi

for file in ../samples/*.java
do
    filename=$(basename "$file")
    echo -n "Testando: $filename... "

    output=$(mvn -f ../pom.xml exec:java -q "-Dexec.mainClass=br.ufc.minijava.Main" "-Dexec.args=../samples/$filename" 2>&1)

    echo "$output" > "test_results/$filename.log"

    if echo "$output" | grep -qi "sucesso"; then
        passou=true
    else
        passou=false
    fi

    if [[ $filename == *"Erro"* ]]; then
        if [ "$passou" = false ]; then
            echo -e "[\e[32mOK\e[0m] (Erro detectado)"
        else
            echo -e "[\e[31mFALHA\e[0m] (Devia falhar)"
        fi
    else
        if [ "$passou" = true ]; then
            echo -e "[\e[32mOK\e[0m] (Sucesso)"
        else
            echo -e "[\e[31mFALHA\e[0m] (Deu erro indevido)"
        fi
    fi
done

echo "------------------------------------------"
echo "Logs limpos e gerados em: scripts/test_results/"