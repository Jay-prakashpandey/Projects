#!/bin/bash

sort_array() {
    local arr=("${!1}")
    local flag=$2
    local res=$(for i in "${arr[@]}"; do echo $i; done | sort -"$flag")
    echo "$res"
}

main() {
    if [[ $# -lt 2 ]]; then
        flag="n"
    else
        flag=$2
    fi
    
    # Convert the string representation of the array back to an array
    IFS=' ' read -r -a array <<< "$1"
    
    sort_array array[@] "$flag"
}

# Convert input parameters into a string representation of an array
input_array=("$@")
array_string="${input_array[*]}"

main "$array_string" "$2"
