if [ ! -f openapi-generator.jar ]; then
    curl https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/3.3.4/openapi-generator-cli-3.3.4.jar --output openapi-generator.jar
fi
java -jar openapi-generator.jar generate -i ansparen-api.yml -g spring -o ../ -c generator.json --import-mappings org.threeten.bp.LocalDate=java.time.LocalDate