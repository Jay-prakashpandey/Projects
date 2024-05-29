#!/bin/bash

fibonacci_up_to_n(){
    local n=$1 p=0 c=1
    for ((i=0;i<=n;i++));do
        echo -n "$p "
        local t=$((c+p))
        p=$c
        c=$t
    done
    echo "\n"
}

fibonacci_up_to_n $@
