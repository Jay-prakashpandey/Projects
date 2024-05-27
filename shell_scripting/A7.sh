#!/bin/bash

largest_array(){
    local max=$1
    shift
    for e in $@
    do
        if [[ $e -gt $max ]]; then
            max=$e
        fi
    done
    echo "max element in {$@} is : $max"
}

main(){
    if [[ $# -lt 1 ]]; then
        echo "Please pass an argument like A7.sh 2 3 4 6 1 4  "
        exit 1
    fi
    largest_array $@
    exit 0
}

main $@