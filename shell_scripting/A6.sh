#!/bin/bash

# Function to evaluate arithmetic expressions
calculator(){
    echo "scale=2; $1" | bc
}

main(){
    if [[ $# -lt 1 ]]; then
        echo "Please pass an argument like A6.sh '2+6'"
        exit 1
    fi

    expression=$1
    echo -n "Answer after solving $expression: "
    calculator "$expression"
    exit 0
}

main "$@"
