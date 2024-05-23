
#varable
v=1

# function to assign arg from run-time else read data
get_value(){
    if [[ $# -lt 1 ]]; then
        read -p "enter the value :" arg
    else
        arg=$1
    fi
    echo $arg
}

# call function

n=$(get_value $1)

for ((i=0;i<n;i++))
    do
        for((j=0;j<=i;j++))
            do
                echo -n "$v "
                ((v++))
            done
        echo ""
    done
exit 0


