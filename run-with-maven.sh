set -o pipefail
mvn clean test | tee ./logs/test-results.log
if [ $? -eq 0 ]; then
    mvn spring-boot:run
    set +o pipefail
else
    set +o pipefail
    exit 1
fi
