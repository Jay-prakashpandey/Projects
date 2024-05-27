#!/bin/bash

# adding integers
add_rational(){
    local sum=$(($1+$2))
    echo $sum
}

# complex parser
parse_complex(){
    local num=$1
    local real=$(echo $num | awk -F '[+i]' '{print $1}')
    local img=$(echo $num | awk -F '[+i]' '{print $2}')
    echo "$real $img"
}

#adding rational
add_irational(){
    local r_a r_b i_a i_b
    read r_a i_a <<< $(parse_complex $1)
    read r_b i_b <<< $(parse_complex $2)
    r_sum=$(($r_a+$r_b))
    i_sum=$(($i_a+$i_b))
    echo "${r_sum}+${i_sum}i"
}

main(){

    read -p "enter first number: " a
    read -p "enter second number: " b
    if [[ $a == *"i"* ]] || [[ $b == *"i"* ]];then
        sum=$(add_irational $a $b)
    else
        sum=$(add_rational $a $b)
    fi
    echo "sum of $a + $b = $sum"

    exit 0
}

main