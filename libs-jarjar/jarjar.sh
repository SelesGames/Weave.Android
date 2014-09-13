#!/bin/bash
 
set -ex
 
rm -f repackaged-*.jar 2> /dev/null
for jar in `ls *.jar | grep -v 'jarjar-'`; do
  java -jar jarjar-* process jarjar.rules "$jar" "repackaged-${jar}"
done
