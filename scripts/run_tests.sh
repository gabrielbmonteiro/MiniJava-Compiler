#!/bin/bash

cd "$(dirname "$0")"

echo -e "\nCompilando o projeto..."
mvn -f ../pom.xml compile -q

echo "------------------------------------------"
echo "Iniciando Bateria de Testes"
echo "------------------------------------------"

mkdir -p test_results

for file in ../samples/*.java
do
    filename=$(basename "$file")
    echo -n "Testando: $filename... "

    mvn -f ../pom.xml exec:java -q "-Dexec.mainClass=br.ufc.minijava.Main" "-Dexec.args=../samples/$filename" > "test_results/$filename.log" 2>&1

    if grep -qi "sucesso" "test_results/$filename.log"; then
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
            echo -e "[\e[31mFALHA\e[0m] (Devia passar)"
        fi
    fi
done

echo "------------------------------------------"
echo "Logs gerados em: scripts/test_results/"