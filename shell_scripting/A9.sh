#!/bin/bash

remove_empty_lines(){
    local file_name=$1
    sed -i '/^$/d' "$file_name"
}

main(){
    if [[ $# -lt 1 ]]; then
        read -p "enter the file_name" file_name
    else
        file_name=$1
    fi

    if [[ -f $file_name ]]; then
        echo "before removing from $file_name"
        cat "$file_name"
        remove_empty_lines "$file_name"
        echo "removed empty lines"
        echo "after remove empty lines from $file_name"
        cat "$file_name"
    else
        echo "enter the valid file_name"
        exit 1
    fi


    exit 0
}

main $@

