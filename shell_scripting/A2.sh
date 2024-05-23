#!/bin/bash

# read n from user or arg

if [[ $# -lt 1 ]]; then
        read -p "enter the value of n : " n

else
        n=$1
fi

for ((i=1; i <= n ; i++))
        do
                for ((j=1;j<=i;j++))
                do
                echo -n "$j "
        done
        echo ""
done
exit 0