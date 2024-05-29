#!/bin/bash

reverse(){
    local tmp=$(echo "$1" |rev)
    local res=$(echo "$tmp+0" |bc)
    echo $res
}

main(){
    echo "reverse of $1 := $(reverse $@)"
}

main $@