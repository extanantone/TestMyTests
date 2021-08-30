#!/bin/sh
./mvnw compile --quiet
for i in $(./mvnw exec:java -Dexec.mainClass="TestMyTestsListInstances" --quiet);
  do echo;
  echo "[NEW INSTANCE]$>" $i;
  echo;
  ./mvnw test -Dtest_instance=$i | grep "\[ERROR\] Tests run:" -A0 -A1;
  echo;
done