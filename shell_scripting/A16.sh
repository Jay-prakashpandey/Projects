#!/bin/bash

greatest_fibinoca_close_n(){
    n=$1

    p=0
    c=0
    nxt=1

    # for (( i=0; i<=n ;i++ ));do or we can
    for ((; c<n ;)); do
    
        # if [[ $c -gt $n ]]; then
        #         exit 0
        # fi
        p=$c
        c=$nxt
        nxt=$((p+c))
    done

    echo $nxt

}

main(){
    greatest_fibinoca_close_n $@
}

main $@
