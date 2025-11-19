mkdir ./logs
logpath=./logs/test-results.log
echo "---" >> "$logpath"
echo "XXXXXXXXXXXXXXXXXXXX" >> "$logpath"
echo "XX    NEW  RUN    XX" >> "$logpath"
echo "XXXXXXXXXXXXXXXXXXXX" >> "$logpath"
echo "---" >> "$logpath"
set -o pipefail
./mvnw clean test | tee -a ./logs/test-results.log
if [ $? -eq 0 ]; then
    ./mvnw -DskipTests spring-boot:run
    set +o pipefail
else
    set +o pipefail
    exit 1
fi
