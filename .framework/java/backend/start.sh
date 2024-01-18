./gradlew -i bootRun &

while true; do
   inotifywait -e modify,create,delete,move -r ./src/ && \
  ./gradlew -i assemble
done
